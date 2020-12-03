package main

import (
	"context"
	"encoding/json"
	"log"

	cestan "github.com/cloudevents/sdk-go/protocol/stan/v2"
	cloudevents "github.com/cloudevents/sdk-go/v2"
)

const (
	cluster = "test-cluster"
	client = "test-reciever"
	subject = "test-subject"
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
	p, err := cestan.NewConsumer(cluster, client, subject, cestan.StanOptions())

	if err != nil {
		log.Fatal(err)
	}

	ctx := context.Background()

	defer p.Close(ctx)

	c, err := cloudevents.NewClient(p, cloudevents.WithTimeNow(), cloudevents.WithUUIDs())

	if err != nil {
		log.Printf("ERROR failed new client %v", err)
		return
	}

	err = c.StartReceiver(ctx, receive)

	if err != nil {
		log.Printf("ERROR failed start receive %v", err)
	}
}
