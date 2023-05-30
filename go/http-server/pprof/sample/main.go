package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"os"
	"strings"

	_ "net/http/pprof"

	"github.com/google/uuid"
)

type Item struct {
	ID    string `json:"id"`
	Name  string `json:"name"`
	Value int    `json:"value"`
}

type CreateItem struct {
	Name  string `json:"name"`
	Value int    `json:"value"`
}

func main() {
	port := os.Getenv("APP_PORT")

	if strings.TrimSpace(port) == "" {
		port = "3000"
	}

	http.HandleFunc("/create", func(rw http.ResponseWriter, r *http.Request) {
		var input CreateItem
		err := json.NewDecoder(r.Body).Decode(&input)

		if err != nil {
			rw.WriteHeader(http.StatusBadRequest)
			fmt.Fprintf(rw, "%v", err)
		} else {
			id := uuid.New().String()

			item := Item{id, input.Name, input.Value}

			json.NewEncoder(rw).Encode(item)
		}
	})

	fmt.Printf("start: port=%s\n", port)

	log.Fatal(http.ListenAndServe(":"+port, nil))
}
