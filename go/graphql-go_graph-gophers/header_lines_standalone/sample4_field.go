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
	Item graphql.ID
	Qty  int32
}

type Cart struct {
	ID    graphql.ID
	Items []CartItem
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
	opt := graphql.UseFieldResolvers()
	schema := graphql.MustParseSchema(gqlSchema, &resolver{}, opt)

	q := `
		{
			find(id: "sample4") {
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
