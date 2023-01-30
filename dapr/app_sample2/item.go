package main

import (
	"bytes"
	"encoding/json"
	"errors"
	"fmt"
	"log"
	"net/http"
	"os"
	"strings"
)

type Item struct {
	ID string `json:"id"`
	Price int
}

type State struct {
	Key string
	Value Item
}

func serverError(w http.ResponseWriter, err error) {
	w.WriteHeader(http.StatusInternalServerError)
	fmt.Fprintf(w, "%v", err)
}

func loadState(endpoint string, id string) (Item, error) {
	res, err := http.Get(endpoint + "/" + id)

	var state Item

	if err != nil {
		return state, err
	}

	err = json.NewDecoder(res.Body).Decode(&state)

	if err == nil && state.ID == "" {
		err = errors.New(fmt.Sprintf("not found: id=%s", id))
	}

	return state, err
}

func saveState(endpoint string, state Item) error {
	s, _ := json.Marshal([]State{
		State{state.ID, state},
	})

	body := bytes.NewBuffer(s)

	res, err := http.Post(endpoint, "application/json", body)

	if err != nil {
		return err
	}

	if res.StatusCode != 204 {
		return errors.New(fmt.Sprintf("failed to store state: statusCode=%d", res.StatusCode))
	}

	return nil
}

func main() {
	daprPort := os.Getenv("DAPR_HTTP_PORT")
	port := os.Getenv("APP_HTTP_PORT")

	if port == "" {
		port = "3000"
	}

	daprUrl := fmt.Sprintf("http://localhost:%s/v1.0", daprPort)
	daprStateUrl := daprUrl + "/state/statestore"

	http.HandleFunc("/price/", func(w http.ResponseWriter, r *http.Request) {
		id := strings.TrimPrefix(r.URL.Path, "/price/")

		if id == "" {
			serverError(w, errors.New("invalid id"))
			return			
		}

		switch r.Method {
		case http.MethodGet:
			state, err := loadState(daprStateUrl, id)

			if err != nil {
				serverError(w, err)
				return
			}
	
			json.NewEncoder(w).Encode(state.Price)

		case http.MethodPost, http.MethodPut:
			var price int
		
			err := json.NewDecoder(r.Body).Decode(&price)

			if err != nil {
				serverError(w, err)
				return
			}

			state := Item{id, price}

			err = saveState(daprStateUrl, state)

			if err != nil {
				serverError(w, err)
				return
			}

			json.NewEncoder(w).Encode(state.Price)

		default:
			w.WriteHeader(http.StatusMethodNotAllowed)
		}
	})

	log.Fatal(http.ListenAndServe(":" + port, nil))
}
