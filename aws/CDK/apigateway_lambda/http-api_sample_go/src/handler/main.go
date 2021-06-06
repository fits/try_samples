package main

import (
	"context"
	"github.com/aws/aws-lambda-go/lambda"
)

func handle(ctx context.Context) (string, error) {
	return "ok", nil
}

func main() {
	lambda.Start(handle)
}
