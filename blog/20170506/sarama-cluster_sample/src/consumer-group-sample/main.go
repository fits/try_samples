package main

import (
	"fmt"
	"log"
	"io"
	"os"
	"os/signal"
	"github.com/Shopify/sarama"
	cluster "github.com/bsm/sarama-cluster"
)

func close(trg io.Closer) {
	if err := trg.Close(); err != nil {
		log.Fatalln(err)
	}
}

func makeStopChannel() chan os.Signal {
	ch := make(chan os.Signal, 1)
	signal.Notify(ch, os.Interrupt)

	return ch
}

func main() {
	config := cluster.NewConfig()
	config.Group.Return.Notifications = true
	config.Consumer.Return.Errors = true
	config.Consumer.Offsets.Initial = sarama.OffsetOldest

	topics := []string{os.Args[1]}
	group := os.Args[2]

	consumer, err := cluster.NewConsumer([]string{"localhost:9092"}, group, topics, config)

	if err != nil {
		panic(err)
	}

	defer close(consumer)

	ch := makeStopChannel()

	for {
		select {
			case msg, more := <- consumer.Messages():
				if more {
					fmt.Printf("topic: %s, offset: %d, key: %s, value: %s\n", 
							msg.Topic, msg.Offset, msg.Key, msg.Value)

					consumer.MarkOffset(msg, "")
				}
			case err, more := <- consumer.Errors():
				if more {
					log.Printf("Error: %+v\n", err.Error())
				}
			case ntf, more := <- consumer.Notifications():
				if more {
					log.Printf("Notification: %+v\n", ntf)
				}
			case <- ch:
				return
		}
	}
}
