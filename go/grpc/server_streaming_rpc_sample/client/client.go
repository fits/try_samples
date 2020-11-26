package main

import (
	"context"
	"log"
	"os"
	"strconv"

	"google.golang.org/grpc"
	pb "sample/proto/item"
)

const (
	address = "localhost:50051"
)

func main() {
	value, err := strconv.Atoi(os.Args[1])

	if err != nil {
		log.Fatal(err)
	}

	con, err := grpc.Dial(address, grpc.WithInsecure())

	if err != nil {
		log.Fatal(err)
	}

	defer con.Close()

	c := pb.NewItemManageClient(con)

	req := pb.CreateItemRequest{Value: int32(value)}

	res, err := c.Create(context.Background(), &req)

	if err != nil {
		log.Fatal(err)
	}

	log.Printf("%v\n", res)
}