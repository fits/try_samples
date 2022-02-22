package main

import (
	"encoding/json"
	"fmt"
	"log"

	"github.com/blevesearch/bleve/v2"
)

type Document = map[string]interface{}

func main() {
	query := `+date:>="2022-02-23T02:45:00+09:00" +date:<"2022-02-23T11:35:00+09:00"`

	items := []string{
		`{"id":"doc1","color":"white","date":"2022-02-22T18:30:00+09:00"}`,
		`{"id":"doc2","color":"black","date":"2022-02-22T18:15:00Z"}`,
		`{"id":"doc3","color":"white","date":"2022-02-23T02:45:00+09:00"}`,
		`{"id":"doc4","color":"black","date":"2022-02-22T06:10:00+09:00"}`,
		`{"id":"doc5","color":"blue","date":"2022-02-23T11:35:00+09:00"}`,
	}

	mapping := bleve.NewIndexMapping()
	mapping.DefaultAnalyzer = "keyword"

	index, err := bleve.New("", mapping)

	if err != nil {
		log.Fatal(err)
	}

	for _, item := range items {
		var d Document
		err = json.Unmarshal([]byte(item), &d)

		if err != nil {
			log.Fatal(err)
		}

		_ = index.Index(fmt.Sprintf("%s", d["id"]), d)
	}

	req := bleve.NewSearchRequest(bleve.NewQueryStringQuery(query))
	req.Fields = []string{"id", "color", "date"}

	res, err := index.Search(req)

	if err != nil {
		log.Fatal(err)
	}

	for _, r := range res.Hits {
		fmt.Printf("%v\n", r.Fields)
	}

	println("-----")

	fd, _ := index.FieldDict("date")

	for {
		de, _ := fd.Next()

		if de == nil {
			break
		}

		fmt.Printf("%v\n", de)
	}
}
