package main

import (
	"context"
	"fmt"
	"os"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/dynamodb"
)

const (
	region    = "ap-northeast-1"
	endpoint  = "http://localhost:8000"
	tableName = "Items"
)

func main() {
	cfg := aws.NewConfig().WithRegion(region).WithEndpoint(endpoint)

	ses := session.Must(session.NewSession(cfg))

	svc := dynamodb.New(ses)

	ctx := context.Background()

	item := map[string]*dynamodb.AttributeValue{
		"id": {
			S: aws.String("item-1"),
		},
		"value": {
			N: aws.String("12"),
		},
	}

	_, err := svc.PutItemWithContext(ctx, &dynamodb.PutItemInput{
		TableName: aws.String(tableName),
		Item:      item,
	})

	if err != nil {
		fmt.Printf("failed: %v\n", err)
		os.Exit(1)
	}

	println("success")
}
