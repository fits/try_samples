package main

import (
	"context"
	"io"
	"log"
	"os"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/s3"
)

func main() {
	endpoint := os.Getenv("S3_ENDPOINT")
	bucket := os.Getenv("BUCKET_NAME")

	key := os.Args[1]
	dest := os.Args[2]

	resolver := aws.EndpointResolverWithOptionsFunc(func(service, region string, opts ...interface{}) (aws.Endpoint, error) {

		if service == s3.ServiceID && len(endpoint) > 0 {
			return aws.Endpoint{
				URL:               endpoint,
				HostnameImmutable: true,
			}, nil
		}

		return aws.Endpoint{}, &aws.EndpointNotFoundError{}
	})

	cfg, err := config.LoadDefaultConfig(
		context.TODO(),
		config.WithEndpointResolverWithOptions(resolver),
	)

	if err != nil {
		log.Fatal(err)
	}

	svc := s3.NewFromConfig(cfg)

	res, err := svc.GetObject(context.TODO(), &s3.GetObjectInput{
		Bucket: &bucket,
		Key:    &key,
	})

	if err != nil {
		log.Fatal(err)
	}

	fw, err := os.Create(dest)

	if err != nil {
		log.Fatal(err)
	}

	defer fw.Close()

	_, err = io.Copy(fw, res.Body)

	if err != nil {
		log.Fatal(err)
	}
}
