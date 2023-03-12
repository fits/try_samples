package main

import (
	"encoding/json"
	"fmt"
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

	r, _ := json.Marshal(rows)

	fmt.Print(string(r))
}
