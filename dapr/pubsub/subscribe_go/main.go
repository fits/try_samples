package main

import (
	"context"
	"log"
	"os"

	"github.com/dapr/go-sdk/service/common"
	daprd "github.com/dapr/go-sdk/service/http"
)

func main() {
	port := os.Getenv("APP_PORT")

	pname := os.Getenv("PUBSUB_NAME")
	topic := os.Getenv("TOPIC_NAME")

	if port == "" {
		port = "3000"
	}

	sub := &common.Subscription{
		PubsubName: pname,
		Topic:      topic,
		Route:      "/handle",
	}

	s := daprd.NewService(":" + port)

	err := s.AddTopicEventHandler(sub, handler)

	if err != nil {
		log.Fatal(err)
	}

	err = s.Start()

	if err != nil {
		log.Fatal(err)
	}
}

func handler(ctx context.Context, ev *common.TopicEvent) (retry bool, err error) {
	log.Printf("received: %s", ev.Data)

	return false, nil
}
