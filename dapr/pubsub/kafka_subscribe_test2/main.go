package main

import (
	"bufio"
	"context"
	"os"

	"github.com/dapr/components-contrib/pubsub"
	"github.com/dapr/components-contrib/pubsub/kafka"
	"github.com/dapr/kit/logger"
)

type Session struct {
	topic  string
	cancel context.CancelFunc
}

func main() {
	broker := "localhost:9092"
	consumerGroup := os.Args[1]

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

	store := map[string]Session{}

	s := bufio.NewScanner(os.Stdin)

	for s.Scan() {
		t := s.Text()

		if t == "q" {
			break
		} else if len(t) > 0 {
			v, ok := store[t]

			if ok {
				logger.Infof("cancel subscribe: topic=%s", t)

				v.cancel()
				delete(store, t)
			} else {
				ctx, cancel := context.WithCancel(context.Background())

				logger.Infof("start subscribe: topic=%s", t)

				r := pubsub.SubscribeRequest{
					Topic: t,
				}

				err = k.Subscribe(ctx, r, func(ctx context.Context, msg *pubsub.NewMessage) error {
					logger.Info(msg)
					return nil
				})

				if err != nil {
					logger.Error(err)
				} else {
					store[t] = Session{topic: t, cancel: cancel}
				}
			}
		}
	}

	logger.Info("end")

	for _, v := range store {
		v.cancel()
	}
}
