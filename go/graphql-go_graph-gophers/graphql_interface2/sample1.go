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

type Event interface {
	ID() graphql.ID
}

type Created struct {
	id    string
	title string
}

func (c *Created) ID() graphql.ID {
	return graphql.ID(c.id)
}

func (c *Created) Title() string {
	return c.title
}

type Deleted struct {
	id     string
	reason string
}

func (d *Deleted) ID() graphql.ID {
	return graphql.ID(d.id)
}

func (d *Deleted) Reason() *string {
	if d.reason == "" {
		return nil
	}

	return &d.reason
}

type eventResolver struct {
	Event
}

func (r *eventResolver) ToCreated() (*Created, bool) {
	c, ok := r.Event.(*Created)
	return c, ok
}

func (r *eventResolver) ToDeleted() (*Deleted, bool) {
	d, ok := r.Event.(*Deleted)
	return d, ok
}

type resolver struct{}

func (r *resolver) Events() []*eventResolver {
	return []*eventResolver{
		{&Created{id: "i-1", title: "sample1"}},
		{&Deleted{id: "i-1"}},
		{&Created{id: "i-2", title: "sample2"}},
		{&Created{id: "i-3", title: "sample3"}},
		{&Deleted{id: "i-3", reason: "test"}},
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
