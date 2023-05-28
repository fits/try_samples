package main

import (
	"log"
	"net/http"
	"strconv"
	"time"
)

func main() {
	http.HandleFunc("/", func(rw http.ResponseWriter, r *http.Request) {
		rw.Write([]byte("golang-" + strconv.FormatInt(time.Now().UnixMilli(), 10)))
	})

	log.Fatal(http.ListenAndServe(":3000", nil))
}
