package main

import (
	"context"
	"io"
	"os"
	"log"
	"google.golang.org/grpc"
	pb "sample"
)


func GetenvOr(envName string, defaultValue string) string {
	res := os.Getenv(envName)

	if res != "" {
		return res
	}

	return defaultValue
}

func main() {
	address := GetenvOr("SERVICE_ADDRESS", "localhost:50051")

	clientId := os.Args[1]
	durableName := os.Args[2]

	con, err := grpc.Dial(address, grpc.WithInsecure())

	if err != nil {
		log.Fatalf("error: %v", err)
	}

	defer con.Close()

	client := pb.NewEventNotifyServiceClient(con)

	req := &pb.SubscribeRequest{ClientId: clientId, DurableName: durableName}

	stream, err := client.Subscribe(context.Background(), req)

	if err != nil {
		log.Fatalf("error: %v", err)
	}

	for {
		msg, err := stream.Recv()

		if err == io.EOF {
			break
		}

		if err != nil {
			log.Fatalf("receive error: %v", err)
		}

		log.Println(msg)
	}
}
