package main

import (
	"encoding/json"
	"log"
	"net/http"

	"github.com/elastic/go-elasticsearch/v8"
	graphql "github.com/graph-gophers/graphql-go"
	"github.com/graph-gophers/graphql-go/relay"
)

const (
	index = "items"

	gqlSchema = `
		type PageInfo {
			total: Int!
			size: Int!
			from: Int!
		}

		type Item {
			id: ID!
			name: String!
			value: Int!
		}

		type ItemConnection {
			edges: [Item!]!
			pageInfo: PageInfo!
		}

		type Query {
			items(size: Int, from: Int): ItemConnection!
		}
	`
)

type Item struct {
	ID    graphql.ID
	Name  string
	Value int32
}

type PageInfo struct {
	Total int32
	Size  int32
	From  int32
}

type ItemConnection struct {
	Edges []Item
	PageInfo
}

type resolver struct {
	db *elasticsearch.Client
}

type ItemsInput struct {
	Size *int32
	From *int32
}

func orInt32(p *int32, d int32) int32 {
	if p != nil {
		return *p
	}
	return d
}

func (r *resolver) Items(args ItemsInput) (*ItemConnection, error) {
	db := r.db

	size := orInt32(args.Size, 10)
	from := orInt32(args.From, 0)

	res, err := db.Search(
		db.Search.WithIndex(index),
		db.Search.WithSize(int(size)),
		db.Search.WithFrom(int(from)),
	)

	if err != nil {
		return nil, err
	}
	defer res.Body.Close()

	var docs struct {
		Hits struct {
			Total struct {
				Value int32
			}
			Hits []struct {
				ID     string `json:"_id"`
				Source struct {
					Name  string
					Value int32
				} `json:"_source"`
			}
		}
	}

	err = json.NewDecoder(res.Body).Decode(&docs)

	if err != nil {
		return nil, err
	}

	es := make([]Item, 0, len(docs.Hits.Hits))

	for _, v := range docs.Hits.Hits {
		es = append(
			es,
			Item{
				ID:    graphql.ID(v.ID),
				Name:  v.Source.Name,
				Value: v.Source.Value,
			},
		)
	}

	p := ItemConnection{
		Edges: es,
		PageInfo: PageInfo{
			Total: docs.Hits.Total.Value,
			Size:  size,
			From:  from,
		},
	}

	return &p, nil
}

func main() {
	es, err := elasticsearch.NewDefaultClient()

	if err != nil {
		log.Fatal(err)
	}

	resolver := resolver{es}

	opts := graphql.UseFieldResolvers()
	schema := graphql.MustParseSchema(gqlSchema, &resolver, opts)

	http.Handle("/graphql", &relay.Handler{Schema: schema})

	log.Fatal(http.ListenAndServe(":4000", nil))
}
