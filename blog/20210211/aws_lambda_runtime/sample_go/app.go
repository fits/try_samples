package main

import (
	"context"
	"fmt"

	"github.com/aws/aws-lambda-go/lambda"
	"github.com/aws/aws-lambda-go/lambdacontext"
)

func handler(ctx context.Context, event interface{}) (string, error) {
	c, _ := lambdacontext.FromContext(ctx)

	fmt.Println("*** call handler")
	fmt.Printf("event = %#v\n", event)
	fmt.Printf("context = %#v\n", c)

	return "sample-go", nil
}

func main() {
	lambda.Start(handler)
}
