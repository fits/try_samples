package main

import (
	"context"
	"encoding/json"

	graphql "github.com/graph-gophers/graphql-go"
)

const (
	schemaString = `
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
	id    string
	value int32
}

func (i *item) ID() graphql.ID {
	return graphql.ID(i.id)
}

func (i *item) Value() int32 {
	return i.value
}

type resolver struct{}

func (r *resolver) One() *item {
	return &item{"item-1", 12}
}

func main() {
	schema := graphql.MustParseSchema(schemaString, new(resolver))

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
