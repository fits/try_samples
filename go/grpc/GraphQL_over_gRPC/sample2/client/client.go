package main

import (
	"context"
	"encoding/json"
	"log"
	"os"
	"strconv"

	"google.golang.org/grpc"
	"google.golang.org/protobuf/types/known/structpb"
	pb "sample/proto/gql"
)

const (
	address = "localhost:50051"
)

func main() {
	category := os.Args[1]
	value, err := strconv.Atoi(os.Args[2])

	if err != nil {
		log.Fatal(err)
	}

	con, err := grpc.Dial(address, grpc.WithInsecure())

	if err != nil {
		log.Fatalf("error: %v", err)
	}

	defer con.Close()

	client := pb.NewGraphQLClient(con)

	ctx := context.Background()

	q1 := `
		mutation createItem($category: Category!, $value: Int!) {
			create(input: { category: $category, value: $value }) {
				id
			}
		}
	`

	s1, err := structpb.NewStruct(map[string]interface{}{
		"category": category, 
		"value": value,
	})

	if err != nil {
		log.Fatal(err)
	}

	v1 := structpb.NewStructValue(s1)

	r1, err := client.Query(ctx, &pb.QueryRequest{Query: q1, Variables: v1})

	if err != nil {
		log.Fatalf("graphql failed: %v", err)
	}

	b1, err := r1.MarshalJSON()

	if err != nil {
		log.Fatalf("marshal failed: %v", err)
	}

	log.Println(string(b1))

	var res1 struct {
		Data struct {
			Create struct {
				ID string
			}
		}
	}

	err = json.Unmarshal(b1, &res1)

	if err != nil {
		log.Fatalf("unmarshal failed: %v", err)
	}

	id := res1.Data.Create.ID

	log.Println("created id =", id)

	q2 := `
		query findItem($id: ID!) {
			find(id: $id) {
				id
				category
				value
			}
		}
	`
	s2, err := structpb.NewStruct(map[string]interface{}{"id": id})

	if err != nil {
		log.Fatal(err)
	}

	v2 := structpb.NewStructValue(s2)

	r2, err := client.Query(ctx, &pb.QueryRequest{Query: q2, Variables: v2})

	if err != nil {
		log.Fatalf("graphql failed: %v", err)
	}

	b2, err := r2.MarshalJSON()

	if err != nil {
		log.Fatalf("marshal failed: %v", err)
	}

	log.Println(string(b2))
}
