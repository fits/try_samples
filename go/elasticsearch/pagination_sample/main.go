package main

import (
	"encoding/json"
	"fmt"
	"log"
	"strings"

	"github.com/elastic/go-elasticsearch/v8"
)

const index = "items"

func main() {
	es, err := elasticsearch.NewDefaultClient()

	if err != nil {
		log.Fatal(err)
	}

	res, err := es.Search(
		es.Search.WithIndex(index),
		es.Search.WithSize(5),
		es.Search.WithFrom(3),
		es.Search.WithBody(strings.NewReader(`{
			"query": {
				"range": {
					"value": { "gt": 300 }
				}
			}
		}`)),
		es.Search.WithSort("value"),
	)

	if err != nil {
		log.Fatal(err)
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
		log.Fatal(err)
	}

	fmt.Printf("total: %d\n", docs.Hits.Total.Value)

	for _, d := range docs.Hits.Hits {
		fmt.Printf("%v\n", d)
	}
}
