package main

import (
	"context"
	"log"

	"google.golang.org/grpc"
	pb "sample/proto/item"
)

const (
	address = "localhost:50051"
)

func main() {
	con, err := grpc.Dial(address, grpc.WithInsecure())

	if err != nil {
		log.Fatal(err)
	}

	defer con.Close()

	c := pb.NewItemManageClient(con)

	stream, err := c.Watch(context.Background(), &pb.WatchItemRequest{})

	if err != nil {
		log.Fatal(err)
	}

	go func() {
		for {
			item, err := stream.Recv()

			if err != nil {
				log.Printf("failed receive: %#v\n", err)
				return
			}

			log.Printf("received: %v\n", item)
		}
	}()

	<-stream.Context().Done()

	log.Println("stream done")
}
