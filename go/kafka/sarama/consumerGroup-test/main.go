package main

import (
	"context"
	"fmt"
	"log"
	"os"
	"os/signal"
	"strconv"
	"time"

	"github.com/IBM/sarama"
)

type handler struct {
	sleepTime int
}

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

	s := fmt.Sprintf("topic=%s, partition=%d, value=%s", msg.Topic, msg.Partition, m)

	log.Printf("start: %s", s)

	time.Sleep(time.Duration(h.sleepTime) * time.Millisecond)

	log.Printf("end: %s", s)
}

func main() {
	broker := os.Getenv("KAFKA_BROKER")

	group := os.Args[1]
	topic := os.Args[2]

	if broker == "" {
		broker = "localhost:9092"
	}

	sleepTime, err := strconv.Atoi(os.Getenv("SLEEP_TIME"))

	if err != nil {
		sleepTime = 1000
	}

	config := sarama.NewConfig()
	cg, err := sarama.NewConsumerGroup([]string{broker}, group, config)

	if err != nil {
		log.Fatal(err)
	}

	quit := make(chan os.Signal, 1)
	signal.Notify(quit, os.Interrupt)

	ctx := context.Background()
	h := handler{sleepTime}

	go func() {
		cg.Consume(ctx, []string{topic}, &h)
	}()

	<-quit

	cg.Close()
}
