package main

import (
	"context"
	"encoding/json"
	"fmt"

	graphql "github.com/graph-gophers/graphql-go"
)

const (
	gqlSchema = `
		interface Cart {
			id: ID!
		}

		type EmptyCart implements Cart {
			id: ID!
		}

		type ActiveCart implements Cart {
			id: ID!
			items: [CartItem!]!
		}

		type CartItem {
			item: ID!
			qty: Int!
		}

		type Query {
			findAll: [Cart!]!
		}
	`
)

type CartItem struct {
	item string
	qty int32
}

func (i CartItem) Item() graphql.ID {
	return graphql.ID(i.item)
}

func (i CartItem) Qty() int32 {
	return i.qty
}

type EmptyCart interface {
	ID() graphql.ID
}

type ActiveCart interface {
	ID() graphql.ID
	Items() []CartItem
}

type cart struct {
	id graphql.ID
	items []CartItem
}

func (c *cart) ID() graphql.ID {
	return c.id
}

func (c *cart) Items() []CartItem {
	return c.items
}

func (c *cart) ToEmptyCart() (EmptyCart, bool) {
	if len(c.items) == 0 {
		return c, true
	}

	return nil, false
}

func (c *cart) ToActiveCart() (ActiveCart, bool) {
	if len(c.items) > 0 {
		return c, true
	}

	return nil, false
}

type resolver struct {}

func (r *resolver) FindAll() []*cart {
	return []*cart{
		&cart{id: "empty-1"},
		&cart{
			"active-1", 
			[]CartItem{ CartItem{"item-1", 1}, CartItem{"item-2", 2} },
		},
		&cart{id: "empty-2"},
		&cart{
			"active-2", 
			[]CartItem{ CartItem{"item-1", 3} },
		},
	}
}

func main() {
	schema := graphql.MustParseSchema(gqlSchema, &resolver{})

	q := `
		{
			findAll {
				__typename
				id
				... on ActiveCart {
					items {
						item
						qty
					}
				}
			}
		}
	`	

	r := schema.Exec(context.Background(), q, "", nil)
	b, err := json.Marshal(r)

	if err != nil {
		panic(err)
	}

	fmt.Println(string(b))
}
