package main

import (
	"context"
	"fmt"
	"log"
	"os"

	"github.com/dapr/go-sdk/client"
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

	if pname == "" {
		pname = "pubsub"
	}

	if topic == "" {
		topic = "sample"
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

	c, err := client.NewClient()

	if err != nil {
		log.Fatal(err)
	}

	err = s.AddServiceInvocationHandler("/publish", func(ctx context.Context, in *common.InvocationEvent) (out *common.Content, err error) {
		log.Printf("published: %s...", in.Data[:min(20, len(in.Data))])

		err = c.PublishEvent(ctx, pname, topic, in.Data)

		if err != nil {
			return nil, err
		}

		out = &common.Content{
			Data:        []byte("ok"),
			ContentType: "text/plain",
		}

		return
	})

	if err != nil {
		log.Fatal(err)
	}

	err = s.Start()

	if err != nil {
		log.Fatal(err)
	}
}

func handler(ctx context.Context, ev *common.TopicEvent) (retry bool, err error) {
	msg := fmt.Sprintf("%v", ev.Data)

	log.Printf("received: %s", msg)

	return false, nil
}
