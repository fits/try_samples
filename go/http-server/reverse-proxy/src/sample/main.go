package main

import (
	"fmt"
	"log"
	"net/http"
	"net/http/httputil"
	"os"
)

func reverseProxy(host string) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {

		proxy := httputil.ReverseProxy{Director: func(req *http.Request) {
			req.URL.Scheme = "http"
			req.URL.Host = host

			fmt.Println(req)
		}}

		proxy.ServeHTTP(w, r)
	}
}

func main() {
	fmt.Println(os.Args)

	http.HandleFunc("/a/", reverseProxy(":8081"))
	http.HandleFunc("/b/", reverseProxy(":8082"))

	log.Fatal(http.ListenAndServe(os.Args[1], nil))
}
