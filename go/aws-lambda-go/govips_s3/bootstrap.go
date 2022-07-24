package main

import (
	"bytes"
	"context"
	"encoding/base64"
	"fmt"
	"io"
	"math"
	"os"
	"strconv"
	"strings"
	"time"

	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/s3"
	"github.com/davidbyttow/govips/v2/vips"
)

type Process struct {
	s3Client *s3.Client
	bucket   *string
}

func (p *Process) handler(ctx context.Context, request events.APIGatewayV2HTTPRequest) (events.APIGatewayProxyResponse, error) {

	key := request.RawPath[1:]

	if !strings.HasSuffix(key, ".jpg") {
		return events.APIGatewayProxyResponse{StatusCode: 404}, nil
	}

	width, _ := strconv.Atoi(request.QueryStringParameters["w"])
	height, _ := strconv.Atoi(request.QueryStringParameters["h"])

	t1 := time.Now()

	params := s3.GetObjectInput{
		Bucket: p.bucket,
		Key:    &key,
	}

	res, err := p.s3Client.GetObject(context.TODO(), &params)

	if err != nil {
		return serverError(), err
	}

	fmt.Printf("s3 download: %v ms\n", time.Since(t1).Milliseconds())

	if width <= 0 && height <= 0 {
		buffer := new(bytes.Buffer)
		_, err = io.Copy(buffer, res.Body)

		if err != nil {
			return serverError(), err
		}

		return toResult(buffer.Bytes()), nil
	}

	t2 := time.Now()

	img, err := vips.NewImageFromReader(res.Body)

	if err != nil {
		return serverError(), err
	}

	if width > 0 && height > 0 {
		img.Thumbnail(width, height, vips.InterestingNone)
	} else {
		scale := math.Max(
			float64(width)/float64(img.Width()),
			float64(height)/float64(img.Height()),
		)

		img.Resize(scale, vips.KernelAuto)
	}

	ep := vips.NewDefaultJPEGExportParams()
	buf, _, err := img.Export(ep)

	if err != nil {
		return serverError(), err
	}

	fmt.Printf("sharp resize: %v ms\n", time.Since(t2).Milliseconds())

	return toResult(buf), nil
}

func serverError() events.APIGatewayProxyResponse {
	return events.APIGatewayProxyResponse{StatusCode: 500}
}

func toResult(buf []byte) events.APIGatewayProxyResponse {
	return events.APIGatewayProxyResponse{
		StatusCode:      200,
		Headers:         map[string]string{"Content-Type": "image/jpeg"},
		Body:            base64.StdEncoding.EncodeToString(buf),
		IsBase64Encoded: true,
	}
}

func main() {
	endpoint := os.Getenv("S3_ENDPOINT")
	bucket := os.Getenv("BUCKET_NAME")

	resolver := aws.EndpointResolverWithOptionsFunc(
		func(service, region string, options ...interface{}) (aws.Endpoint, error) {
			if service == s3.ServiceID && len(endpoint) > 0 {
				return aws.Endpoint{
					URL:               endpoint,
					HostnameImmutable: true,
				}, nil
			}

			return aws.Endpoint{}, &aws.EndpointNotFoundError{}
		},
	)

	cfg, _ := config.LoadDefaultConfig(
		context.TODO(), 
		config.WithEndpointResolverWithOptions(resolver)
	)

	vips.LoggingSettings(nil, vips.LogLevelError)

	vips.Startup(nil)
	defer vips.Shutdown()

	proc := Process{
		s3Client: s3.NewFromConfig(cfg),
		bucket:   &bucket,
	}

	lambda.Start(proc.handler)
}
