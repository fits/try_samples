package main

import (
	"context"
	"encoding/json"
	"log"
	"net/http"

	cloudevents "github.com/cloudevents/sdk-go/v2"
)

const (
	address = ":3000"
)

func receive(ctx context.Context, event cloudevents.Event) {
	log.Printf("%s", event)

	buf, err := json.Marshal(event)

	if err != nil {
		log.Printf("ERROR receive: %v", err)
		return
	}

	log.Printf("%s", buf)
}

func main() {
	p, err := cloudevents.NewHTTP()

	if err != nil {
		log.Fatal(err)
	}

	ctx := context.Background()

	r, err := cloudevents.NewHTTPReceiveHandler(ctx, p, receive)

	if err != nil {
		log.Fatal(err)
	}

	log.Printf("start: %s", address)

	err = http.ListenAndServe(address, r)

	if err != nil {
		log.Fatal(err)
	}
}
