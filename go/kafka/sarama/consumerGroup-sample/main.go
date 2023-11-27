package main

import (
	"context"
	"log"
	"os"
	"os/signal"

	"github.com/IBM/sarama"
)

type handler struct{}

func (h *handler) Setup(s sarama.ConsumerGroupSession) error {
	log.Println("called Setup")
	return nil
}

func (h *handler) Cleanup(s sarama.ConsumerGroupSession) error {
	log.Println("called Cleanup")
	return nil
}

func (h *handler) ConsumeClaim(sess sarama.ConsumerGroupSession, claim sarama.ConsumerGroupClaim) error {
	for {
		select {
		case msg, ok := <-claim.Messages():
			if !ok {
				return nil
			}

			h.handleMessage(msg)

			sess.MarkMessage(msg, "")

		case <-sess.Context().Done():
			return nil
		}
	}
}

func (h *handler) handleMessage(msg *sarama.ConsumerMessage) {
	m := msg.Value[:min(20, len(msg.Value))]
	log.Printf("received: topic=%s, partition=%d, value=%s", msg.Topic, msg.Partition, m)
}

func main() {
	broker := os.Getenv("KAFKA_BROKER")

	group := os.Args[1]
	topic := os.Args[2]

	if broker == "" {
		broker = "localhost:9092"
	}

	config := sarama.NewConfig()
	cg, err := sarama.NewConsumerGroup([]string{broker}, group, config)

	if err != nil {
		log.Fatal(err)
	}

	quit := make(chan os.Signal, 1)
	signal.Notify(quit, os.Interrupt)

	ctx := context.Background()
	h := handler{}

	go func() {
		cg.Consume(ctx, []string{topic}, &h)
	}()

	<-quit

	cg.Close()
}
