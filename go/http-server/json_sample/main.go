package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
)

type Document = map[string]interface{}

type Server struct {
	docs []Document
}

func (s *Server) add(doc Document) int {
	id := len(s.docs)
	s.docs = append(s.docs, doc)

	return id
}

type AddResult struct {
	ID int `json:"id"`
}

func main() {
	server := Server{}

	http.HandleFunc("/", func(rw http.ResponseWriter, r *http.Request) {
		switch r.Method {
		case http.MethodGet:
			json.NewEncoder(rw).Encode(server.docs)
		case http.MethodPost:
			var doc Document
			err := json.NewDecoder(r.Body).Decode(&doc)

			if err != nil {
				rw.WriteHeader(http.StatusBadRequest)
				fmt.Fprintf(rw, "%v", err)
			} else {
				id := server.add(doc)

				rw.WriteHeader(http.StatusCreated)
				json.NewEncoder(rw).Encode(AddResult{id})
			}

		default:
			rw.WriteHeader(http.StatusMethodNotAllowed)
		}
	})

	log.Fatal(http.ListenAndServe(":8080", nil))
}
