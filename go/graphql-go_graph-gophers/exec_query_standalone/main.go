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
var mu sync.RWMutex

func pushItem(item Item) {
	mu.Lock()
	store = append(store, item)
	mu.Unlock()
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
	`
	schema := graphql.MustParseSchema(gqlSchema, &resolver{})

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
	println(string(rj1))

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
		query findItem($id: ID!) {
			find(id: $id) {
				id
				category
				value
			}
		}
	`

	vs := map[string]interface{}{"id": res1.Data.Create.ID}

	r2 := schema.Exec(ctx, q2, "", vs)

	rj2, err := json.Marshal(r2)

	if err != nil {
		panic(err)
	}

	var res2 struct {
		Data struct {
			Find struct {
				ID       string
				Category string
				Value    int32
			}
		}
	}

	println(string(rj2))

	err = json.Unmarshal(rj2, &res2)

	if err != nil {
		panic(err)
	}

	fmt.Printf("%#v\n", res2)
}