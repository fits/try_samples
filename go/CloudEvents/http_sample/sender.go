package main

import (
	"context"
	"log"

	cloudevents "github.com/cloudevents/sdk-go/v2"
)

const (
	url = "http://localhost:3000"
)

func main() {
	c, err := cloudevents.NewDefaultClient()

	if err != nil {
		log.Fatal(err)
	}

	event := cloudevents.NewEvent()
	event.SetSource("example:sender-binary-go")
	event.SetType("sample.type")

	data := map[string]string{"value": "g1"}
	event.SetData(cloudevents.ApplicationJSON, data)

	ctx := cloudevents.ContextWithTarget(context.Background(), url)

	res := c.Send(ctx, event)

	if cloudevents.IsUndelivered(res) {
		log.Fatalf("failed send %v", res)
	}

	log.Printf("result: %#v", res)
}
