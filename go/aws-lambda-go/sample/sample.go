package main

import "github.com/aws/aws-lambda-go/lambda"

func handler() (string, error) {
	println("*** called handler")
	return "hello", nil
}

func main() {
	lambda.Start(handler)
}
