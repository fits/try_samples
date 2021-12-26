package main

import (
	"encoding/json"
	"fmt"
	"log"
	"strings"

	"github.com/elastic/go-elasticsearch/v8"
)

const index = "category_samples"

func main() {
	es, err := elasticsearch.NewDefaultClient()

	if err != nil {
		log.Fatal(err)
	}

	query := es.Search.WithBody(strings.NewReader(`{
		"size": 0,
		"aggs": {
			"categories": {
				"nested": { "path": "categories" },
				"aggs": {
					"count": {
						"multi_terms": {
							"terms": [
								{ "field": "categories.code" },
								{ "field": "categories.name.keyword" }
							],
							"order": { "_term": "asc" }
						}
					}
				}
			}
		}
	}`))

	res, err := es.Search(
		es.Search.WithIndex(index),
		query,
		es.Search.WithPretty(),
	)

	if err != nil {
		log.Fatal(err)
	}
	defer res.Body.Close()

	fmt.Println(res)

	var data struct {
		Aggregations struct {
			Categories struct {
				Count struct {
					Buckets []struct{
						KeyAsString string `json:"key_as_string"`
						DocCount    int32  `json:"doc_count"`
					}
				}
			}
		}
	}

	err = json.NewDecoder(res.Body).Decode(&data)

	if err != nil {
		log.Fatal(err)
	}

	for _, d := range data.Aggregations.Categories.Count.Buckets {
		fmt.Printf("key = %s, count = %d\n", d.KeyAsString, d.DocCount)
	}
}
