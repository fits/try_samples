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

type event struct {
	created *Created
	deleted *Deleted
}

func (e *event) ID() graphql.ID {
	if e.created == nil {
		return e.deleted.ID()
	}

	return e.created.ID()
}

func (e *event) ToCreated() (*Created, bool) {
	if e.created == nil {
		return nil, false
	}

	return e.created, true
}

func (e *event) ToDeleted() (*Deleted, bool) {
	if e.deleted == nil {
		return nil, false
	}

	return e.deleted, true
}

type resolver struct{}

func (r *resolver) Events() []*event {
	return []*event{
		{created: &Created{id: "i-1", title: "sample1"}},
		{deleted: &Deleted{id: "i-1"}},
		{created: &Created{id: "i-2", title: "sample2"}},
		{created: &Created{id: "i-3", title: "sample3"}},
		{deleted: &Deleted{id: "i-3", reason: "test"}},
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
