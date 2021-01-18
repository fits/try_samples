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

type cart struct {
	empty  *EmptyCart
	active *ActiveCart
}

func (c *cart) ID() graphql.ID {
	if c.empty != nil {
		return c.empty.ID()
	} else if c.active != nil {
		return c.active.ID()
	}
	return graphql.ID("")
}

func (c *cart) ToEmptyCart() (*EmptyCart, bool) {
	if c.empty != nil {
		return c.empty, true
	}

	return nil, false
}

func (c *cart) ToActiveCart() (*ActiveCart, bool) {
	if c.active != nil {
		return c.active, true
	}

	return nil, false
}

type EmptyCart struct {
	id string
}

func (c *EmptyCart) ID() graphql.ID {
	return graphql.ID(c.id)
}

type ActiveCart struct {
	id string
	items []CartItem
}

func (c *ActiveCart) ID() graphql.ID {
	return graphql.ID(c.id)
}

func (c *ActiveCart) Items() []CartItem {
	return c.items
}

type resolver struct {}

func (r *resolver) FindAll() []*cart {
	return []*cart{
		{empty: &EmptyCart{"empty-1"}},
		{
			active: &ActiveCart{
				"active-1", 
				[]CartItem{ {"item-1", 1}, {"item-2", 2} },
			},
		},
		{empty: &EmptyCart{"empty-2"}},
		{
			active: &ActiveCart{"active-2", []CartItem{ {"item-1", 3} }},
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
