package main

import (
	"fmt"

	"github.com/graphql-go/graphql"
)

type Item struct {
	ID    string
	Value int
}

func main() {
	itemType := graphql.NewObject(graphql.ObjectConfig{
		Name: "Item",
		Fields: graphql.Fields{
			"id": &graphql.Field{
				Type: graphql.NewNonNull(graphql.String),
			},
			"value": &graphql.Field{
				Type: graphql.Int,
			},
		},
	})

	fmt.Println(itemType)

	queryType := graphql.NewObject(graphql.ObjectConfig{
		Name: "Query",
		Fields: graphql.Fields{
			"find": &graphql.Field{
				Type: itemType,
				Args: graphql.FieldConfigArgument{
					"id": &graphql.ArgumentConfig{
						Type: graphql.NewNonNull(graphql.String),
					},
				},
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					id := p.Args["id"].(string)

					item := Item{
						ID:    id,
						Value: 123,
					}

					return item, nil
				},
			},
		},
	})

	schema, err := graphql.NewSchema(graphql.SchemaConfig{Query: queryType})

	if err != nil {
		panic(err)
	}

	query := `
	{
		find(id: "item-1") {
			id
			value
		}
	}
	`

	params := graphql.Params{Schema: schema, RequestString: query}

	r := graphql.Do(params)

	if len(r.Errors) == 0 {
		fmt.Println(r.Data)

		item := r.Data.(map[string]interface{})["find"]

		fmt.Println(item)
	}
}
