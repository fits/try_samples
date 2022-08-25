package main

import (
	"bytes"
	"context"
	"fmt"
	"io"
	"log"
	"strconv"
	"net/http"
	"os"
	"time"

	"github.com/davidbyttow/govips/v2/vips"
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/s3"
)

func main() {
	endpoint := os.Getenv("S3_ENDPOINT")
	bucket := os.Getenv("BUCKET_NAME")

	rslv := aws.EndpointResolverWithOptionsFunc(func(service, region string, options ...interface{}) (aws.Endpoint, error) {
		if service == s3.ServiceID && len(endpoint) > 0 {
			return aws.Endpoint{
				URL: endpoint,
				HostnameImmutable: true,
			}, nil
		}

		return aws.Endpoint{}, &aws.EndpointNotFoundError{}
	})

	cfg, _ := config.LoadDefaultConfig(context.TODO(), config.WithEndpointResolverWithOptions(rslv))

	vips.LoggingSettings(nil, vips.LogLevelError)

	vips.Startup(nil)
	defer vips.Shutdown()

	client := s3.NewFromConfig(cfg)

	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		key := r.URL.Path[1:]

		qparam := r.URL.Query()
		width, _ := strconv.Atoi(qparam.Get("w"))
		height, _ := strconv.Atoi(qparam.Get("h"))

		t1 := time.Now()

		input := s3.GetObjectInput{
			Bucket: &bucket,
			Key: &key,
		}

		res, err := client.GetObject(context.TODO(), &input)

		if err != nil {
			w.WriteHeader(http.StatusNotFound)
			io.WriteString(w, err.Error())
			return
		} 

		buffer := new(bytes.Buffer)
		_, err = io.Copy(buffer, res.Body)

		if err != nil {
			w.WriteHeader(http.StatusInternalServerError)
			io.WriteString(w, err.Error())
			return
		}

		fmt.Printf("s3 download: %v ms\n", time.Since(t1).Milliseconds())

		if width <= 0 || height <= 0 {
			_, err = w.Write(buffer.Bytes())

			if err != nil {
				w.WriteHeader(http.StatusInternalServerError)
				io.WriteString(w, err.Error())
			}

			return
		}

		t2 := time.Now()

		img, err := vips.NewImageFromReader(buffer)

		if err != nil {
			w.WriteHeader(http.StatusInternalServerError)
			io.WriteString(w, err.Error())
			return
		}

		img.Thumbnail(width, height, vips.InterestingNone)

		ep := vips.NewDefaultJPEGExportParams()
		buf, _, err := img.Export(ep)

		if err != nil {
			w.WriteHeader(http.StatusInternalServerError)
			io.WriteString(w, err.Error())
			return
		}

		fmt.Printf("govips thumbnail: %v ms\n", time.Since(t2).Milliseconds())

		w.Write(buf)
	})

	log.Fatal(http.ListenAndServe(":8080", nil))
}
