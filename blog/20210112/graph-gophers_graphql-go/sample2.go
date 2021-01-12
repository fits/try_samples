package main

import (
	"context"
	"encoding/json"

	graphql "github.com/graph-gophers/graphql-go"
)

const (
	gqlSchema = `
		type Item {
			id: ID!
			value: Int!
		}

		type Query {
			one: Item!
		}
	`
)

type item struct {
	ID    graphql.ID
	Value int32
}

type resolver struct{}

func (r *resolver) One() *item {
	return &item{graphql.ID("item-2"), 34}
}

func main() {
	schema := graphql.MustParseSchema(gqlSchema, new(resolver), graphql.UseFieldResolvers())

	q := `
		{
			one {
				id
				value
			}
		}
	`

	r := schema.Exec(context.Background(), q, "", nil)
	b, err := json.Marshal(r)

	if err != nil {
		panic(err)
	}

	println(string(b))
}
