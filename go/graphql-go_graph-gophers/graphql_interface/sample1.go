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

type Cart interface {
	ID() graphql.ID
}

type cartResolver struct {
	Cart
}

func (r *cartResolver) ToEmptyCart() (*EmptyCart, bool) {
	c, ok := r.Cart.(*EmptyCart)
	return c, ok
}

func (r *cartResolver) ToActiveCart() (*ActiveCart, bool) {
	c, ok := r.Cart.(*ActiveCart)
	return c, ok
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

func (r *resolver) FindAll() []*cartResolver {
	return []*cartResolver{
		{&EmptyCart{"empty-1"}},
		{
			&ActiveCart{
				"active-1", 
				[]CartItem{ {"item-1", 1}, {"item-2", 2} },
			},
		},
		{&EmptyCart{"empty-2"}},
		{
			&ActiveCart{"active-2", []CartItem{ {"item-1", 3} }},
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
