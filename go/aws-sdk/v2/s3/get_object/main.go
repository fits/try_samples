package main

import (
	"bytes"
	"context"
	"encoding/base64"
	"fmt"
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

	resolver := aws.EndpointResolverWithOptionsFunc(func(service, region string, opts ...interface{}) (aws.Endpoint, error) {

		if service == s3.ServiceID && len(endpoint) > 0 {
			return aws.Endpoint{
				URL:               endpoint,
				HostnameImmutable: true,
			}, nil
		}

		return aws.Endpoint{}, &aws.EndpointNotFoundError{}
	})

	cfg, err := config.LoadDefaultConfig(context.TODO(), config.WithEndpointResolverWithOptions(resolver))

	if err != nil {
		log.Fatal(err)
	}

	svc := s3.NewFromConfig(cfg)

	params := s3.GetObjectInput{
		Bucket: &bucket,
		Key:    &key,
	}

	res, err := svc.GetObject(context.TODO(), &params)

	if err != nil {
		log.Fatal(err)
	}

	fmt.Printf("%#v \n", res)

	buf := new(bytes.Buffer)
	_, err = io.Copy(buf, res.Body)

	if err != nil {
		log.Fatal(err)
	}

	enc := base64.StdEncoding.EncodeToString(buf.Bytes())

	fmt.Println(enc)
}
