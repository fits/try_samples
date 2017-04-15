package main

import (
	"github.com/Shopify/sarama"
	"fmt"
	"log"
	"io"
	"os"
	"os/signal"
)

func close(trg io.Closer) {
	log.Println("Close: ", trg)

	if err := trg.Close(); err != nil {
		log.Fatalln(err)
	}
}

func main() {
	fmt.Println(os.Args)

	consumer, err := sarama.NewConsumer([]string{"localhost:9092"}, nil)

	if err != nil {
		panic(err)
	}

	defer close(consumer)

	ptConsumer, err := consumer.ConsumePartition(os.Args[1], 0, 
												sarama.OffsetOldest)

	if err != nil {
		panic(err)
	}

	defer close(ptConsumer)

	ch := make(chan os.Signal, 1)
	signal.Notify(ch, os.Interrupt)

	Loop:
		for {
			select {
				case msg := <- ptConsumer.Messages():
					fmt.Printf("topic: %s, offset: %d, key: %s, value: %s\n", 
						msg.Topic, msg.Offset, msg.Key, msg.Value)

				case <- ch:
					break Loop
			}
		}
}
