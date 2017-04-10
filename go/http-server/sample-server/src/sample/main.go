package main

import (
	"log"
	"fmt"
	"net/http"
)

func main() {

	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		fmt.Fprintf(w, "sample page")
	})

	log.Fatal(http.ListenAndServe(":8080", nil))
}
