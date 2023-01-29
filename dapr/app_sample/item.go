package main

import (
	"bytes"
	"encoding/json"
	"errors"
	"fmt"
	"io"
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

func saveState(endpoint string, data Item) error {
	s, _ := json.Marshal([]State{
		State{data.ID, data},
	})

	body := bytes.NewBuffer(s)

	res, err := http.Post(endpoint, "application/json", body)

	if err != nil {
		return err
	}

	if res.StatusCode >= 300 {
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

	http.HandleFunc("/find/", func(w http.ResponseWriter, r *http.Request) {
		id := strings.TrimPrefix(r.URL.Path, "/find/")

		res, err := http.Get(daprStateUrl + "/" + id)

		if err != nil {
			serverError(w, err)
			return
		}

		_, err = io.Copy(w, res.Body)

		if err != nil {
			serverError(w, err)
		}
	})

	http.HandleFunc("/regist", func(w http.ResponseWriter, r *http.Request) {
		var item Item
		json.NewDecoder(r.Body).Decode(&item)

		if item.ID == "" || item.Price < 0 {
			serverError(w, errors.New("invalid request"))
			return
		}

		err := saveState(daprStateUrl, item)

		if err != nil {
			serverError(w, err)
			return
		}

		json.NewEncoder(w).Encode(item)
	})

	log.Fatal(http.ListenAndServe(":" + port, nil))
}
