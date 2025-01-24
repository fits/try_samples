package main

import (
	"bufio"
	"context"
	"os"

	"github.com/dapr/components-contrib/pubsub"
	"github.com/dapr/components-contrib/pubsub/kafka"
	"github.com/dapr/kit/logger"
)

func main() {
	broker := "localhost:9092"
	topic := os.Args[1]
	consumerGroup := os.Args[2]

	logger := logger.NewLogger("kafka-test")
	k := kafka.NewKafka(logger)

	m := pubsub.Metadata{}
	m.Properties = map[string]string{
		"authType":      "none",
		"brokers":       broker,
		"consumerGroup": consumerGroup,
		"initialOffset": "oldest",
	}

	err := k.Init(context.Background(), m)

	if err != nil {
		logger.Error(err)
		return
	}

	var ctx context.Context
	var cancel context.CancelFunc

	s := bufio.NewScanner(os.Stdin)

	for s.Scan() {
		t := s.Text()

		if cancel != nil {
			logger.Info("cancel current subscribe")

			cancel()
			cancel = nil
		}

		if t == "q" {
			break
		} else if t == "s" {
			ctx, cancel = context.WithCancel(context.Background())

			logger.Info("start subscribe")

			r := pubsub.SubscribeRequest{
				Topic: topic,
			}

			err = k.Subscribe(ctx, r, func(ctx context.Context, msg *pubsub.NewMessage) error {
				logger.Info(msg)
				return nil
			})

			if err != nil {
				logger.Error(err)
			}
		}
	}

	logger.Info("end")
}
