package main

import (
	"encoding/json"
	"log"
	"os"

	"github.com/segmentio/parquet-go"
)

func main() {
	file := os.Args[1]

	rows, err := parquet.ReadFile[Trace](file)

	if err != nil {
		log.Fatal(err)
	}

	r, err := json.Marshal(rows)

	if err != nil {
		log.Fatal(err)
	}

	print(string(r))
}
