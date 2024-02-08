package main

import (
	"fmt"
	"log"
	"net/http"
	"os"
	"strings"
)

func main() {
	port := os.Getenv("APP_PORT")

	if strings.TrimSpace(port) == "" {
		port = "3000"
	}

	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		log.Printf("url query: %s", r.URL.RawQuery)
		fmt.Fprintf(w, "ok")
	})

	log.Fatal(http.ListenAndServe(":"+port, nil))
}
