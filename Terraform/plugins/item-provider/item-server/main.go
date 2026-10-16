package main

import (
	"encoding/json"
	"fmt"
	"log"
	"math/rand"
	"net/http"
	"os"
)

var store = map[string]Item{}

type Item struct {
	Id    string `json:"id"`
	Value int32  `json:"value"`
}

type ItemInput struct {
	Value int32 `json:"value"`
}

func main() {
	addr := ":8000"

	if len(os.Args) > 1 {
		addr = os.Args[1]
	}

	router := http.NewServeMux()

	router.HandleFunc("POST /items", createUser)
	router.HandleFunc("GET /items/{id}", getUser)
	router.HandleFunc("PUT /items/{id}", updateUser)
	router.HandleFunc("DELETE /items/{id}", deleteUser)

	err := http.ListenAndServe(addr, router)

	if err != nil {
		log.Fatal(err)
	}
}

func createUser(w http.ResponseWriter, r *http.Request) {
	log.Print("create user")

	id := fmt.Sprintf("id-%d", rand.Intn(1000))

	_, ok := store[id]

	if ok {
		w.WriteHeader(http.StatusInternalServerError)
	} else {
		var input ItemInput
		err := json.NewDecoder(r.Body).Decode(&input)

		if err != nil {
			http.Error(w, err.Error(), http.StatusBadRequest)
			return
		}

		item := Item{id, input.Value}
		store[id] = item

		err = json.NewEncoder(w).Encode(item)

		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
		}
	}
}

func getUser(w http.ResponseWriter, r *http.Request) {
	id := r.PathValue("id")

	log.Printf("get user: id=%s", id)

	item, ok := store[id]

	if ok {
		err := json.NewEncoder(w).Encode(item)

		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
		}
	} else {
		w.WriteHeader(http.StatusNotFound)
	}
}

func updateUser(w http.ResponseWriter, r *http.Request) {
	id := r.PathValue("id")

	log.Printf("update user: id=%s", id)

	item, ok := store[id]

	if ok {
		var input ItemInput
		err := json.NewDecoder(r.Body).Decode(&input)

		if err != nil {
			http.Error(w, err.Error(), http.StatusBadRequest)
			return
		}

		item.Value = input.Value

		err = json.NewEncoder(w).Encode(item)

		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
		} else {
			store[id] = item
		}
	} else {
		w.WriteHeader(http.StatusNotFound)
	}
}

func deleteUser(w http.ResponseWriter, r *http.Request) {
	id := r.PathValue("id")

	log.Printf("delete user: id=%s", id)

	_, ok := store[id]

	if ok {
		delete(store, id)
	} else {
		w.WriteHeader(http.StatusNotFound)
	}
}
