package main

import (
	"context"
	"encoding/json"
	"net"
	"log"
	"sync"

	"github.com/google/uuid"
	graphql "github.com/graph-gophers/graphql-go"

	"google.golang.org/grpc"
	"google.golang.org/protobuf/types/known/structpb"
	pb "sample/proto/gql"
)

const (
	address = ":50051"
)

var store []Item
var subscribes []chan<- *Item

var mu sync.RWMutex
var subMu sync.RWMutex

func pushItem(item Item) {
	mu.Lock()
	store = append(store, item)
	mu.Unlock()

	go publish(&item)
}

func findItem(id graphql.ID) *Item {
	mu.RLock()
	defer mu.RUnlock()

	for _, r := range store {
		if r.ID() == id {
			return &r
		}
	}

	return nil
}

func subscribe(ch chan<- *Item) {
	log.Println("*** subscribe")

	subMu.Lock()
	subscribes = append(subscribes, ch)
	subMu.Unlock()
}

func unsubscribe(ch chan<- *Item) {
	log.Println("*** unsubscribe")

	var tmp []chan<- *Item

	subMu.Lock()
	defer subMu.Unlock()

	for _, s := range subscribes {
		if s != ch {
			tmp = append(tmp, s)
		}
	}

	subscribes = tmp
}

func publish(item *Item) {
	subMu.RLock()
	defer subMu.RUnlock()

	for _, s := range subscribes {
		s <- item
	}
}

type Item struct {
	id       graphql.ID
	category string
	value    int32
}

func (r *Item) ID() graphql.ID {
	return r.id
}

func (r *Item) Category() string {
	return r.category
}

func (r *Item) Value() int32 {
	return r.value
}

type CreateItem struct {
	Category string
	Value    int32
}

type resolver struct {}

func (_ *resolver) Create(args struct { Input CreateItem }) (*Item, error) {
	log.Printf("call create: %v\n", args)

	id, err := uuid.NewRandom()

	if err != nil {
		return nil, err
	}

	item := Item{graphql.ID(id.String()), args.Input.Category, args.Input.Value}

	pushItem(item)

	return &item, nil
}

func (_ *resolver) Find(args struct { ID graphql.ID }) *Item {
	log.Printf("call find: %v\n", args)
	return findItem(args.ID)
}

func (_ *resolver) Created(ctx context.Context) <-chan *Item {
	log.Println("subscribe created")

	ch := make(chan *Item)
	subscribe(ch)

	go func() {
		<- ctx.Done()
		log.Println("created context done")

		unsubscribe(ch)
		close(ch)
	}()

	return ch
}

type server struct {
	pb.UnimplementedGraphQLServer
	schema *graphql.Schema
}

func toStruct(res interface{}) (*structpb.Struct, error) {
	buf, err := json.Marshal(res)

	if err != nil {
		return nil, err
	}

	s, err := structpb.NewStruct(nil)

	if err != nil {
		return nil, err
	}

	err = s.UnmarshalJSON(buf)

	if err != nil {
		return nil, err
	}

	return s, nil
}

func (s *server) Query(ctx context.Context, req *pb.QueryRequest) (*structpb.Struct, error) {
	log.Printf("call Query: %v\n", req)

	var vs map[string]interface{}

	if req.GetVariables().GetStructValue() != nil {
		vs = req.GetVariables().GetStructValue().AsMap()
	}

	res := s.schema.Exec(ctx, req.GetQuery(), "", vs)

	return toStruct(res)
}

func (s *server) Subscription(req *pb.QueryRequest, stream pb.GraphQL_SubscriptionServer) error {
	log.Printf("call Subscription: %v\n", req)

	var vs map[string]interface{}

	if req.GetVariables().GetStructValue() != nil {
		vs = req.GetVariables().GetStructValue().AsMap()
	}

	ch, err := s.schema.Subscribe(stream.Context(), req.GetQuery(), "", vs)

	if err != nil {
		return err
	}

	for {
		res, ok := <-ch

		if !ok {
			log.Println("channel closed")
			return nil
		}

		s, err := toStruct(res)

		if err != nil {
			return err
		}

		stream.Send(s)
	}
}

func main() {
	listen, err := net.Listen("tcp", address)

	if err != nil {
		log.Fatalf("error: %v", err)
	}

	s := grpc.NewServer()

	gqlSchema := `
		enum Category {
			Standard
			Extra
		}

		input CreateItem {
			category: Category!
			value: Int!
		}

		type Item {
			id: ID!
			category: Category!
			value: Int!
		}

		type Mutation {
			create(input: CreateItem!): Item
		}

		type Query {
			find(id: ID!): Item
		}

		type Subscription {
			created: Item
		}
	`
	schema := graphql.MustParseSchema(gqlSchema, &resolver{})

	pb.RegisterGraphQLServer(s, &server{schema: schema})

	log.Println("server start:", address)

	if err := s.Serve(listen); err != nil {
		log.Fatalf("failed serve: %v", err)
	}
}
