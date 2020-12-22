package main

import (
	"context"
	"fmt"
	"log"
	"net/http"
	"os"
	"os/signal"
)

func main() {
	mux := http.NewServeMux()

	mux.HandleFunc("/", func(w http.ResponseWriter, req *http.Request) {
		fmt.Fprint(w, "sample")
	})

	var err error

	srv := http.Server{Addr: ":8080", Handler: mux}

	go func() {
		sig := make(chan os.Signal, 1)
		signal.Notify(sig, os.Interrupt)
		<-sig

		log.Print("shutdown")

		err = srv.Shutdown(context.Background())

		if err != nil {
			log.Printf("shutdown error: %v", err)
		}
	}()

	err = srv.ListenAndServe()

	if err != nil && err != http.ErrServerClosed {
		log.Printf("listen error: %v", err)
	}

	log.Print("end")
}
