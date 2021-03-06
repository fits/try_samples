package main

import (
	"context"
	"encoding/json"
	"fmt"

	graphql "github.com/graph-gophers/graphql-go"
)

const (
	gqlSchema = `
		type CartItem {
			item: ID!
			qty: Int!
		}

		type Cart {
			id: ID!
			items: [CartItem!]!
		}

		type Query {
			find(id: ID!): Cart
		}
	`
)

type CartItem struct {
	item graphql.ID
	qty  int32
}

func (i CartItem) Item() graphql.ID {
	return i.item
}

func (i CartItem) Qty() int32 {
	return i.qty
}

type Cart struct {
	id    graphql.ID
	items []CartItem
}

func (c *Cart) ID() graphql.ID {
	return c.id
}

func (c *Cart) Items() []CartItem {
	return c.items
}

type resolver struct {}

func (r *resolver) Find(args struct { ID graphql.ID }) *Cart {
	items := []CartItem{
		CartItem{"item-1", 10},
		CartItem{"item-2", 20},
		CartItem{"item-3", 30},
	}

	cart := Cart{args.ID, items}

	return &cart
}

func main() {
	schema := graphql.MustParseSchema(gqlSchema, &resolver{})

	q := `
		{
			find(id: "sample-1") {
				id
				items {
					item
					qty
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
