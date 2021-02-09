package main

import (
	"context"
	"fmt"

	"github.com/aws/aws-lambda-go/lambda"
	"github.com/aws/aws-lambda-go/lambdacontext"
)

type Item struct {
	Name string
}

func handler(ctx context.Context, item Item) (string, error) {
	c, _ := lambdacontext.FromContext(ctx)

	fmt.Printf("*** called handler: CONTEXT = %#v, ITEM = %#v", c, item)

	return "hello", nil
}

func main() {
	lambda.Start(handler)
}
