package main

import (
	"context"
	"encoding/json"
	"log"

	cloudevents "github.com/cloudevents/sdk-go/v2"
)

const (
	port = 3000
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

	p.Port = port

	ctx := context.Background()

	c, err := cloudevents.NewClient(p)

	if err != nil {
		log.Fatal(err)
	}

	log.Printf("start: %d", p.Port)

	err = c.StartReceiver(ctx, receive)

	if err != nil {
		log.Fatal(err)
	}
}
