package main

import (
	"context"
	"log"
	"os"
	"time"

	"google.golang.org/grpc"
	pb "sample1"
)

func main() {
	address := "localhost:50051"

	con, err := grpc.Dial(address, grpc.WithInsecure())

	if err != nil {
		log.Fatalf("error: %v", err)
	}

	defer con.Close()

	c := pb.NewSampleServiceClient(con)

	msg := os.Args[1]

	ctx, cancel := context.WithTimeout(context.Background(), time.Second)
	defer cancel()

	r, err := c.Call(ctx, &pb.SampleRequest{Message: msg})

	if err != nil {
		log.Fatalf("failed: %v", err)
	}

	log.Println(r.Message)
}
