package main

import (
	"fmt"
	"log"
	"net/http"
	"io/ioutil"
	"os"
)

func main() {
	fmt.Println(os.Args)

	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {

		fmt.Println("----- request info -----")
		fmt.Println(r)

		if buf, err := ioutil.ReadAll(r.Body); err == nil {
			fmt.Println("----- request body -----")
			fmt.Println(string(buf))
		}

		w.Header().Set("Access-Control-Allow-Origin", "*")

		fmt.Fprintf(w, "sample page")
	})

	log.Fatal(http.ListenAndServe(os.Args[1], nil))
}
