package main

import (
	"encoding/json"
	"fmt"
	"log"

	"github.com/blevesearch/bleve/v2"
)

type Document = map[string]interface{}

func main() {
	query := `+color:white +price:>=500`

	items := []string{
		`{"id":"doc1","color":"white","price":100,"date":"2022-02-22T18:30:00+09:00"}`,
		`{"id":"doc2","color":"black","price":2000,"date":"2022-02-22T18:15:00Z"}`,
		`{"id":"doc3","color":"white","price":30000,"date":"2022-02-23T02:45:00+09:00"}`,
		`{"id":"doc4","color":"black","price":4000,"date":"2022-02-22T06:10:00+09:00"}`,
		`{"id":"doc5","color":"white","price":500,"date":"2022-02-23T11:35:00+09:00"}`,
	}

	mapping := bleve.NewIndexMapping()
	mapping.DefaultMapping.Dynamic = false

	cf := bleve.NewKeywordFieldMapping()
	mapping.DefaultMapping.AddFieldMappingsAt("color", cf)

	df := bleve.NewDateTimeFieldMapping()
	df.Index = false
	mapping.DefaultMapping.AddFieldMappingsAt("date", df)

	pf := bleve.NewNumericFieldMapping()
	pf.IncludeInAll = false
	pf.IncludeTermVectors = false
	mapping.DefaultMapping.AddFieldMappingsAt("price", pf)

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
	req.Fields = []string{"color", "date", "price"}

	res, err := index.Search(req)

	if err != nil {
		log.Fatal(err)
	}

	for _, r := range res.Hits {
		fmt.Printf("%v\n", r.Fields)
	}

	showFields(index)
}

func showFields(index bleve.Index) {
	fs, _ := index.Fields()

	for _, f := range fs {
		fmt.Printf("----- field: %v -----\n", f)

		fd, err := index.FieldDict(f)

		if err != nil {
			log.Fatal(err)
		}

		for {
			de, err := fd.Next()

			if de == nil {
				break
			}

			if err != nil {
				log.Fatal(err)
			}

			fmt.Printf("%v \n", de)
		}
	}
}
