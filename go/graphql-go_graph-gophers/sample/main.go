package main

import (
	"log"
	"net/http"
	"sync"

	"github.com/google/uuid"
	graphql "github.com/graph-gophers/graphql-go"
	"github.com/graph-gophers/graphql-go/relay"
)

var store []Item
var mu sync.Mutex

func pushItem(item Item) {
	mu.Lock()
	store = append(store, item)
	mu.Unlock()
}

func findItem(id graphql.ID) *Item {
	mu.Lock()
	defer mu.Unlock()

	for _, r := range store {
		if r.ID() == id {
			return &r
		}
	}

	return nil
}

type Item struct {
	id graphql.ID
	category string
	value int32
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
	Value int32
}

type resolver struct {}

func (_ *resolver) Create(args struct { Input CreateItem }) (*Item, error) {
	log.Printf("call create: %#v", args)

	id, err := uuid.NewRandom()

	if err != nil {
		return nil, err
	}

	item := Item{graphql.ID(id.String()), args.Input.Category, args.Input.Value}

	go pushItem(item)

	return &item, nil
}

func (_ *resolver) Find(args struct { ID graphql.ID }) *Item {
	log.Printf("call find: %#v", args)
	return findItem(args.ID)
}

func main() {
	s := `
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
	schema := graphql.MustParseSchema(s, &resolver{})

	http.Handle("/graphql", &relay.Handler{Schema: schema})

	err := http.ListenAndServe(":8080", nil)

	log.Fatal(err)
}
