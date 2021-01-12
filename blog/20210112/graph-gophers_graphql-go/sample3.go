package main

import (
	"context"
	"encoding/json"
	"log"
	"sync"

	"github.com/google/uuid"
	graphql "github.com/graph-gophers/graphql-go"
)

const (
	schemaString = `
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
)

type createItem struct {
	Category string
	Value    int32
}

type item struct {
	id       string
	category string
	value    int32
}

func (i *item) ID() graphql.ID {
	return graphql.ID(i.id)
}

func (i *item) Category() string {
	return i.category
}

func (i *item) Value() int32 {
	return i.value
}

type store struct {
	sync.RWMutex
	items []*item
}

func (s *store) add(i *item) {
	s.Lock()
	defer s.Unlock()

	s.items = append(s.items, i)
}

func (s *store) get(id graphql.ID) *item {
	s.RLock()
	defer s.RUnlock()

	for _, i := range s.items {
		if i.ID() == id {
			return i
		}
	}

	return nil
}

type broker struct {
	sync.RWMutex
	subscribes []chan<- *item
}

func (b *broker) subscribe(ch chan<- *item) {
	log.Println("[INFO] subscribe")

	b.Lock()
	defer b.Unlock()

	b.subscribes = append(b.subscribes, ch)
}

func (b *broker) unsubscribe(ch chan<- *item) {
	log.Println("[INFO] unsubscribe")

	var tmp []chan<- *item

	b.Lock()
	defer b.Unlock()

	for _, s := range b.subscribes {
		if s != ch {
			tmp = append(tmp, s)
		}
	}

	b.subscribes = tmp
}

func (b *broker) publish(i *item) {
	b.RLock()
	defer b.RUnlock()

	for _, s := range b.subscribes {
		s <- i
	}
}

type resolver struct {
	store  *store
	broker *broker
}

func (r *resolver) Create(args struct{ Input createItem }) (*item, error) {
	id, err := uuid.NewRandom()

	if err != nil {
		return nil, err
	}

	i := item{id.String(), args.Input.Category, args.Input.Value}

	r.store.add(&i)

	go func() {
		r.broker.publish(&i)
	}()

	return &i, nil
}

func (r *resolver) Find(args struct{ ID graphql.ID }) *item {
	return r.store.get(args.ID)
}

func (r *resolver) Created(ctx context.Context) <-chan *item {
	ch := make(chan *item)
	r.broker.subscribe(ch)

	go func() {
		<-ctx.Done()
		log.Println("[INFO] context done")

		r.broker.unsubscribe(ch)
		close(ch)
	}()

	return ch
}

func onCreated(ch <-chan interface{}) {
	for {
		r, ok := <-ch

		if !ok {
			log.Println("[INFO] closed channel")
			return
		}

		b, _ := json.Marshal(r)

		log.Println("[SUBSCRIPTION]", string(b))
	}
}

func printResponse(r *graphql.Response) error {
	b, err := json.Marshal(r)

	if err != nil {
		return err
	}

	log.Println(string(b))

	return nil
}

func main() {
	resolver := resolver{new(store), new(broker)}
	schema := graphql.MustParseSchema(schemaString, &resolver)

	ctx, cancel := context.WithCancel(context.Background())

	s := `
		subscription {
			created {
				id
				category
				value
			}
		}
	`

	ch, err := schema.Subscribe(ctx, s, "", nil)

	if err != nil {
		panic(err)
	}

	go onCreated(ch)

	m1 := `
		mutation {
			create(input: { category: Standard, value: 10 }) {
				id
			}
		}
	`

	mr1 := schema.Exec(context.Background(), m1, "", nil)
	_ = printResponse(mr1)

	var cr1 struct {
		Create struct {
			ID string
		}
	}

	err = json.Unmarshal(mr1.Data, &cr1)

	if err != nil {
		panic(err)
	}

	q := `
		query findItem($id: ID!) {
			find(id: $id) {
				id
				category
				value
			}
		}
	`

	qr1 := schema.Exec(context.Background(), q, "",
		map[string]interface{}{"id": cr1.Create.ID})

	_ = printResponse(qr1)

	m2 := `
		mutation Create($p: CreateItem!) {
			create(input: $p) {
				id
			}
		}
	`

	vs := map[string]interface{}{
		"p": map[string]interface{}{
			"category": "Extra",
			"value":    123,
		},
	}

	mr2 := schema.Exec(context.Background(), m2, "", vs)
	_ = printResponse(mr2)

	var cr2 map[string]map[string]interface{}

	err = json.Unmarshal(mr2.Data, &cr2)

	if err != nil {
		panic(err)
	}

	qr2 := schema.Exec(context.Background(), q, "", cr2["create"])
	_ = printResponse(qr2)

	cancel()

	mr3 := schema.Exec(context.Background(), m2, "", map[string]interface{}{
		"p": map[string]interface{}{
			"category": "Extra",
			"value":    987,
		},
	})
	_ = printResponse(mr3)

	mr4 := schema.Exec(context.Background(), m2, "", map[string]interface{}{
		"p": map[string]interface{}{
			"category": "Standard",
			"value":    567,
		},
	})
	_ = printResponse(mr4)

	qr5 := schema.Exec(context.Background(), q, "",
		map[string]interface{}{"id": "invalid-id"})

	_ = printResponse(qr5)
}
