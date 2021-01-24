package main

import (
	"context"
	"encoding/json"

	graphql "github.com/graph-gophers/graphql-go"
)

const (
	schemaString = `
		union Event = Created | Deleted

		type Created {
			id: ID!
			title: String!
		}

		type Deleted {
			id: ID!
			reason: String
		}

		type Query {
			events: [Event!]!
		}
	`
)

type event interface{}

type created struct {
	id    string
	title string
}

func (c *created) ID() graphql.ID {
	return graphql.ID(c.id)
}

func (c *created) Title() string {
	return c.title
}

type deleted struct {
	id     string
	reason string
}

func (d *deleted) ID() graphql.ID {
	return graphql.ID(d.id)
}

func (d *deleted) Reason() *string {
	if d.reason == "" {
		return nil
	}

	return &d.reason
}

type eventResolver struct {
	event
}

func (r *eventResolver) ToCreated() (*created, bool) {
	c, ok := r.event.(*created)
	return c, ok
}

func (r *eventResolver) ToDeleted() (*deleted, bool) {
	d, ok := r.event.(*deleted)
	return d, ok
}

type resolver struct{}

func (r *resolver) Events() []*eventResolver {
	return []*eventResolver{
		{&created{id: "i-1", title: "sample1"}},
		{&deleted{id: "i-1"}},
		{&created{id: "i-2", title: "sample2"}},
		{&created{id: "i-3", title: "sample3"}},
		{&deleted{id: "i-3", reason: "test"}},
	}
}

func main() {
	schema := graphql.MustParseSchema(schemaString, new(resolver))

	q := `
		{
			events {
				__typename
				... on Created {
					id
					title
				}
				... on Deleted {
					id
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
