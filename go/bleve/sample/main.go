package main

import (
	"github.com/blevesearch/bleve/v2"
)

type Event struct {
	Id      string
	Message string
}

func index() {
	mapping := bleve.NewIndexMapping()
	index, err := bleve.New("sample.bleve", mapping)

	if err != nil {
		println(err.Error())
		return
	}

	defer index.Close()

	d1 := Event{"event-1", "assigned item-1"}
	index.Index(d1.Id, d1)

	d2 := Event{"event-2", "assigned item2"}
	index.Index(d2.Id, d2)

	d3 := Event{"event-3", "shipped item-3"}
	index.Index(d3.Id, d3)
}

func query(q string) {
	index, _ := bleve.Open("sample.bleve")
	defer index.Close()

	query := bleve.NewQueryStringQuery(q)

	req := bleve.NewSearchRequest(query)
	res, _ := index.Search(req)

	for _, hit := range res.Hits {
		println(hit.ID)
	}
}

func main() {
	index()

	query("item")
	println("-----")
	query("item-1")
}
