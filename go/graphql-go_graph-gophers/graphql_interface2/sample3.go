package main

import (
	"context"
	"encoding/json"

	graphql "github.com/graph-gophers/graphql-go"
)

const (
	schemaString = `
		interface Event {
			id: ID!
		}

		type Created implements Event {
			id: ID!
			title: String!
		}

		type Deleted implements Event {
			id: ID!
			reason: String
		}

		type Query {
			events: [Event!]!
		}
	`
)

type Created interface {
	ID() graphql.ID
	Title() string
}

type Deleted interface {
	ID() graphql.ID
	Reason() *string
}

type event struct {
	id     string
	title  string
	reason string
	del    bool
}

func (e *event) ID() graphql.ID {
	return graphql.ID(e.id)
}

func (e *event) Title() string {
	return e.title
}

func (e *event) Reason() *string {
	if e.reason == "" {
		return nil
	}

	return &e.reason
}

func (e *event) ToCreated() (Created, bool) {
	if e.del {
		return nil, false
	}

	return e, true
}

func (e *event) ToDeleted() (Deleted, bool) {
	if e.del {
		return e, true
	}

	return nil, false
}

type resolver struct{}

func (r *resolver) Events() []*event {
	return []*event{
		{id: "i-1", title: "sample1"},
		{id: "i-1", del: true},
		{id: "i-2", title: "sample2"},
		{id: "i-3", title: "sample3"},
		{id: "i-3", reason: "test", del: true},
	}
}

func main() {
	schema := graphql.MustParseSchema(schemaString, new(resolver))

	q := `
		{
			events {
				__typename
				id
				... on Created {
					title
				}
				... on Deleted {
					reason
				}
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
