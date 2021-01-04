package main

import (
	"context"
	"encoding/json"
	"sync"

	graphql "github.com/graph-gophers/graphql-go"
)

const (
	gqlSchema = `
		interface Item {
			id: ID!
		}

		type SimpleItem implements Item {
			id: ID!
		}

		type WithVariantsItem implements Item {
			id: ID!
			variants: [Variant!]!
		}

		enum Category {
			Color
			Size
		}

		type Variant {
			category: Category!
			value: String!
		}

		input VariantInput {
			category: Category!
			value: String!
		}

		input CreateItem {
			id: ID!
			variants: [VariantInput!]
		}

		type Mutation {
			create(input: CreateItem!): Item
		}

		type Query {
			find(id: ID!): Item
		}
	`
)

type SimpleItem interface {
	ID() graphql.ID
}

type WithVariantsItem interface {
	ID() graphql.ID
	Variants() []Variant
}

type Variant struct {
	category string
	value    string
}

func (v Variant) Category() string {
	return v.category
}

func (v Variant) Value() string {
	return v.value
}

type item struct {
	id       graphql.ID
	variants []Variant
}

func (i *item) ID() graphql.ID {
	return i.id
}

func (i *item) Variants() []Variant {
	return i.variants
}

type VariantInput struct {
	Category string
	Value    string
}

type CreateItem struct {
	ID       graphql.ID
	Variants *[]VariantInput
}

func (i *item) ToSimpleItem() (SimpleItem, bool) {
	if len(i.variants) == 0 {
		return i, true
	}

	return nil, false
}

func (i *item) ToWithVariantsItem() (WithVariantsItem, bool) {
	if len(i.variants) > 0 {
		return i, true
	}

	return nil, false
}

type store struct {
	items []*item
	mutex sync.RWMutex
}

func (s *store) saveItem(i *item) bool {
	s.mutex.Lock()
	defer s.mutex.Unlock()

	if i == nil || s.findItem(i.ID()) != nil {
		return false
	}

	s.items = append(s.items, i)
	return true
}

func (s *store) loadItem(id graphql.ID) *item {
	s.mutex.RLock()
	defer s.mutex.RUnlock()

	return s.findItem(id)
}

func (s *store) findItem(id graphql.ID) *item {
	for _, i := range s.items {
		if i.ID() == id {
			return i
		}
	}

	return nil
}

type resolver struct {
	store
}

func (r *resolver) Create(args struct{ Input CreateItem }) *item {
	i := item{id: args.Input.ID}

	if args.Input.Variants != nil {
		for _, v := range *args.Input.Variants {
			i.variants = append(i.variants, Variant{v.Category, v.Value})
		}
	}

	if r.store.saveItem(&i) {
		return &i
	}

	return nil
}

func (r *resolver) Find(args struct{ ID graphql.ID }) *item {
	return r.store.loadItem(args.ID)
}

func main() {
	ctx := context.Background()
	schema := graphql.MustParseSchema(gqlSchema, &resolver{})

	q := `
		query FindItem($id: ID!) {
			find(id: $id) {
				__typename
				id
				... on WithVariantsItem {
					variants {
						category
						value
					}
				}
			}
		}
	`

	r := schema.Exec(ctx, q, "", map[string]interface{}{"id": "item-0"})
	b, err := json.Marshal(r)

	if err != nil {
		panic(err)
	}

	println(string(b))

	q1 := `
		mutation CreateItem($id: ID!) {
			create(input: {id: $id}) {
				__typename
				id
			}
		}
	`

	r1 := schema.Exec(ctx, q1, "", map[string]interface{}{"id": "item-1"})
	b1, err := json.Marshal(r1)

	if err != nil {
		panic(err)
	}

	println(string(b1))

	r11 := schema.Exec(ctx, q, "", map[string]interface{}{"id": "item-1"})
	b11, err := json.Marshal(r11)

	if err != nil {
		panic(err)
	}

	println(string(b11))

	r12 := schema.Exec(ctx, q1, "", map[string]interface{}{"id": "item-1"})
	b12, err := json.Marshal(r12)

	if err != nil {
		panic(err)
	}

	println(string(b12))

	q2 := `
		mutation {
			create(input: {id: "item-2", variants: [{category: Color, value: "White"}, {category: Size, value: "Free"}]}) {
				__typename
				id
			}
		}
	`

	r2 := schema.Exec(ctx, q2, "", nil)
	b2, err := json.Marshal(r2)

	if err != nil {
		panic(err)
	}

	println(string(b2))

	r21 := schema.Exec(ctx, q, "", map[string]interface{}{"id": "item-2"})
	b21, err := json.Marshal(r21)

	if err != nil {
		panic(err)
	}

	println(string(b21))
}
