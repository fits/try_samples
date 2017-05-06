package main

import (
	"fmt"
	"log"
	"io"
	"os"
	"os/signal"
	"github.com/Shopify/sarama"
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
	consumer, err := sarama.NewConsumer([]string{"localhost:9092"}, nil)

	if err != nil {
		panic(err)
	}

	defer close(consumer)

	ptConsumer, err := consumer.ConsumePartition(os.Args[1], 0, sarama.OffsetOldest)

	if err != nil {
		panic(err)
	}

	defer close(ptConsumer)

	ch := makeStopChannel()

	for {
		select {
			case msg := <- ptConsumer.Messages():
				fmt.Printf("topic: %s, offset: %d, key: %s, value: %s\n", 
					msg.Topic, msg.Offset, msg.Key, msg.Value)

			case <- ch:
				return
		}
	}
}
