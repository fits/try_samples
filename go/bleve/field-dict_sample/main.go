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

	mapping := bleve.NewIndexMapping()
	mapping.IndexDynamic = false

	for _, f := range []string{"name", "color"} {
		fm := bleve.NewKeywordFieldMapping()
		mapping.DefaultMapping.AddFieldMappingsAt(f, fm)
	}

	index, err := bleve.New("", mapping)

	if err != nil {
		log.Fatal(err)
	}

	err = indexFromFile(index, file)

	if err != nil {
		log.Fatal(err)
	}

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
