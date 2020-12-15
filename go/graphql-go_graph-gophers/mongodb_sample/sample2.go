package main

import (
	"context"
	"log"
	"net/http"

	"github.com/google/uuid"
	graphql "github.com/graph-gophers/graphql-go"
	"github.com/graph-gophers/graphql-go/relay"

	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
)

const (
	mongoUri = "mongodb://localhost"
	dbName = "sample"
	colName = "items"

	gqlSchema = `
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
	`
)

type Item struct {
	ID       graphql.ID `bson:"_id"`
	Category string
	Value    int32
}

type CreateItem struct {
	Category string
	Value    int32
}

type store struct {
	context    context.Context
	collection *mongo.Collection
}

func (s *store) pushItem(item Item) error {
	_, err := s.collection.InsertOne(s.context, item)
	return err
}

func (s *store) findItem(id graphql.ID) *Item {
	query := bson.M{"_id": id}

	var item Item
	s.collection.FindOne(s.context, query).Decode(&item)

	return &item
}

type resolver struct {
	store
}

func (r *resolver) Create(args struct { Input CreateItem }) (*Item, error) {
	log.Printf("call create: %#v", args)

	id, err := uuid.NewRandom()

	if err != nil {
		return nil, err
	}

	item := Item{graphql.ID(id.String()), args.Input.Category, args.Input.Value}

	err = r.store.pushItem(item)

	if err != nil {
		return nil, err
	}

	return &item, nil
}

func (r *resolver) Find(args struct { ID graphql.ID }) *Item {
	log.Printf("call find: %#v", args)
	return r.store.findItem(args.ID)
}

func main() {
	ctx := context.Background()

	opts := options.Client().ApplyURI(mongoUri)
	client, err := mongo.Connect(ctx, opts)

	if err != nil {
		log.Fatal(err)
	}

	defer client.Disconnect(ctx)

	col := client.Database(dbName).Collection(colName)
	store := store{ ctx, col }

	gqlOpt := graphql.UseFieldResolvers()
	schema := graphql.MustParseSchema(gqlSchema, &resolver{ store }, gqlOpt)

	http.Handle("/graphql", &relay.Handler{Schema: schema})

	err = http.ListenAndServe(":8080", nil)

	if err != nil {
		log.Printf("ERROR %v", err)
		return
	}
}
