package main

import (
	"context"
	"encoding/json"
	"fmt"
	"sync"

	"github.com/google/uuid"
	graphql "github.com/graph-gophers/graphql-go"
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
	println("*** subscribe")

	subMu.Lock()
	defer subMu.Unlock()

	subscribes = append(subscribes, ch)
}

func unsubscribe(ch chan<- *Item) {
	println("*** unsubscribe")

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
	fmt.Printf("call create: %#v\n", args)

	id, err := uuid.NewRandom()

	if err != nil {
		return nil, err
	}

	item := Item{graphql.ID(id.String()), args.Input.Category, args.Input.Value}

	pushItem(item)

	return &item, nil
}

func (_ *resolver) Find(args struct { ID graphql.ID }) *Item {
	fmt.Printf("call find: %#v\n", args)
	return findItem(args.ID)
}

func (_ *resolver) Created(ctx context.Context) <-chan *Item {
	println("subscribe created")

	ch := make(chan *Item)
	subscribe(ch)

	go func() {
		<- ctx.Done()
		println("created context Done")

		unsubscribe(ch)
		close(ch)
	}()

	return ch
}

func onMessage(ch <-chan interface{}) {
	for {
		r, ok := <-ch

		if !ok {
			println("*** closed channel")
			return
		}

		rj, err := json.Marshal(r)

		if err != nil {
			panic(err)
		}

		fmt.Println("*** received:", string(rj))
	}
}

func main() {
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

	subCtx, cancel := context.WithCancel(context.Background())

	s1 := `
		subscription {
			created {
				id
				category
				value
			}
		}
	`

	ch, err := schema.Subscribe(subCtx, s1, "", nil)

	if err != nil {
		panic(err)
	}

	go onMessage(ch)

	ctx := context.Background()

	q1 := `
		mutation {
			create(input: { category: Extra, value: 123 }) {
				id
			}
		}
	`

	r1 := schema.Exec(ctx, q1, "", nil)
	rj1, err := json.Marshal(r1)

	if err != nil {
		panic(err)
	}

	fmt.Printf("%#v\n", rj1)
	fmt.Println(string(rj1))

	var res1 struct {
		Data struct {
			Create struct {
				ID string
			}
		}
	}
	
	err = json.Unmarshal(rj1, &res1)

	if err != nil {
		panic(err)
	}

	fmt.Println("id =", res1.Data.Create.ID)

	println("-----")

	q2 := `
		mutation {
			create(input: { category: Standard, value: 456 }) {
				id
				category
				value
			}
		}
	`

	r2 := schema.Exec(ctx, q2, "", nil)
	rj2, err := json.Marshal(r2)

	if err != nil {
		panic(err)
	}

	fmt.Println(string(rj2))

	cancel() // cancel subscription

	println("-----")

	q3 := `
		query findItem($id: ID!) {
			find(id: $id) {
				id
				category
				value
			}
		}
	`

	vs := map[string]interface{}{"id": res1.Data.Create.ID}

	r3 := schema.Exec(ctx, q3, "", vs)

	rj3, err := json.Marshal(r3)

	if err != nil {
		panic(err)
	}

	var res3 struct {
		Data struct {
			Find struct {
				ID       string
				Category string
				Value    int32
			}
		}
	}

	fmt.Println(string(rj3))

	err = json.Unmarshal(rj3, &res3)

	if err != nil {
		panic(err)
	}

	fmt.Printf("%#v\n", res3)
}