package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"os"
	"strings"
)

type Data struct {
	Name    string `json:"name"`
	Counter int    `json:"counter"`
}

func main() {
	port := os.Getenv("APP_PORT")
	host, _ := os.Hostname()

	if strings.TrimSpace(port) == "" {
		port = "3000"
	}

	d := Data{Name: host}

	http.HandleFunc("/", func(rw http.ResponseWriter, r *http.Request) {
		json.NewEncoder(rw).Encode(d)
	})

	http.HandleFunc("/up", func(rw http.ResponseWriter, r *http.Request) {
		d.Counter++
		json.NewEncoder(rw).Encode(d)
	})

	fmt.Printf("start: port=%s\n", port)

	log.Fatal(http.ListenAndServe(":"+port, nil))
}
