package main

import (
	"context"
	"log"

	cestan "github.com/cloudevents/sdk-go/protocol/stan/v2"
	cloudevents "github.com/cloudevents/sdk-go/v2"
)

const (
	cluster = "test-cluster"
	client = "test-sender"
	subject = "test-subject"
)

const (
	url = "http://localhost:3000"
)

func main() {
	s, err := cestan.NewSender(cluster, client, subject, cestan.StanOptions())

	if err != nil {
		log.Fatal(err)
	}

	ctx := context.Background()

	defer s.Close(ctx)

	c, err := cloudevents.NewClient(s, cloudevents.WithTimeNow(), cloudevents.WithUUIDs())

	if err != nil {
		log.Printf("ERROR new client %v", err)
		return
	}

	event := cloudevents.NewEvent()
	event.SetSource("example:sender-go")
	event.SetType("sample.type")

	data := map[string]string{"value": "g1"}
	event.SetData(cloudevents.ApplicationJSON, data)

	res := c.Send(ctx, event)

	if cloudevents.IsUndelivered(res) {
		log.Fatalf("failed send %v", res)
	}

	log.Printf("result: %#v", res)
}
