package main

import (
	"bufio"
	"encoding/json"
	"fmt"
	"log"
	"os"

	"github.com/blevesearch/bleve/v2"
)

type Document = map[string]interface{}

func main() {
	file := os.Args[1]
	query := os.Args[2]

	mapping := bleve.NewIndexMapping()
	mapping.DefaultAnalyzer = "keyword"

	index, err := bleve.New("", mapping)

	if err != nil {
		log.Fatal(err)
	}

	err = indexFromFile(index, file)

	if err != nil {
		log.Fatal(err)
	}

	req := bleve.NewSearchRequest(bleve.NewQueryStringQuery(query))
	req.Fields = []string{"name", "color"}

	res, err := index.Search(req)

	if err != nil {
		log.Fatal(err)
	}

	fmt.Printf("total: %d\n", res.Total)

	for _, d := range res.Hits {
		fmt.Printf("id=%v, fields=%v\n", d.ID, d.Fields)
	}
}

func indexFromFile(index bleve.Index, file string) error {
	f, err := os.Open(file)

	if err != nil {
		return err
	}
	defer f.Close()

	scanner := bufio.NewScanner(f)

	for scanner.Scan() {
		var d Document
		err = json.Unmarshal(scanner.Bytes(), &d)

		if err != nil {
			return err
		}

		id := fmt.Sprintf("%v", d["id"])
		err = index.Index(id, d)

		if err != nil {
			return err
		}
	}

	return nil
}
