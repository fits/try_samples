package main

import (
	"encoding/json"
	"log"
	"net/http"
	"os"
	"strings"
)

type daprConfig struct {
	Entities         []string
	ActorIdleTimeout string
}

func main() {
	port := os.Getenv("APP_HTTP_PORT")

	if port == "" {
		port = "3000"
	}

	http.HandleFunc("/dapr/config", func(w http.ResponseWriter, r *http.Request) {
		log.Print("called dapr config")

		config := daprConfig{[]string{"sampleActor"}, "30s"}

		json.NewEncoder(w).Encode(config)
	})

	http.HandleFunc("/healthz", func(w http.ResponseWriter, r *http.Request) {
		log.Print("called healthz")

		w.WriteHeader(http.StatusOK)
	})

	http.HandleFunc("/actors/", func(w http.ResponseWriter, r *http.Request) {
		path := strings.TrimPrefix(r.URL.Path, "/actors/")

		log.Printf("called actors: %s", path)

		w.WriteHeader(http.StatusOK)
	})

	log.Fatal(http.ListenAndServe(":"+port, nil))
}
