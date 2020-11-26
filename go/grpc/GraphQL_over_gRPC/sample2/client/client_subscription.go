package main

import (
	"context"
	"log"

	"google.golang.org/grpc"
	"google.golang.org/protobuf/types/known/structpb"
	pb "sample/proto/gql"
)

const (
	address = "localhost:50051"
)

func main() {
	con, err := grpc.Dial(address, grpc.WithInsecure())

	if err != nil {
		log.Fatalf("error: %v", err)
	}

	defer con.Close()

	client := pb.NewGraphQLClient(con)

	ctx := context.Background()

	q := `
		subscription {
			created {
				id
				category
				value
			}
		}
	`
	v := structpb.NewNullValue()

	stream, err := client.Subscription(ctx, &pb.QueryRequest{Query: q, Variables: v})

	if err != nil {
		log.Fatalf("graphql failed: %v", err)
	}

	go func() {
		for {
			res, err := stream.Recv()

			if err != nil {
				log.Printf("failed receive: %v\n", err)
				return
			}

			buf, err := res.MarshalJSON()

			if err != nil {
				log.Println("%v\n", err)
				return
			}

			log.Printf("received: %v\n", string(buf))
		}
	}()

	<-stream.Context().Done()

	log.Println("stream done")
}
