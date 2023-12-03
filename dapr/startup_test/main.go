package main

import (
	"context"
	"errors"
	"fmt"
	"log"
	"os"
	"strconv"
	"time"

	"github.com/dapr/go-sdk/service/common"
	daprd "github.com/dapr/go-sdk/service/http"
)

func main() {
	port := os.Getenv("APP_PORT")

	if port == "" {
		port = "3000"
	}

	delay, err := strconv.Atoi(os.Getenv("DELAY_SECOND"))

	if err != nil {
		delay = 1
	}

	s := daprd.NewService(":" + port)

	started := false

	err = s.AddServiceInvocationHandler("/info", func(ctx context.Context, in *common.InvocationEvent) (out *common.Content, err error) {
		out = &common.Content{
			Data:        []byte(fmt.Sprintf("started:%t", started)),
			ContentType: "text/plain",
		}

		return
	})

	if err != nil {
		log.Fatal(err)
	}

	err = s.AddServiceInvocationHandler("/started", func(ctx context.Context, in *common.InvocationEvent) (out *common.Content, err error) {
		log.Printf("called started: %t", started)

		if !started {
			return nil, errors.New("is starting")
		}

		out = &common.Content{
			Data:        []byte("ok"),
			ContentType: "text/plain",
		}

		return
	})

	if err != nil {
		log.Fatal(err)
	}

	go func() {
		log.Print("sleep start")

		time.Sleep(time.Duration(delay) * time.Second)
		started = true

		log.Print("sleep end")
	}()

	err = s.Start()

	if err != nil {
		log.Fatal(err)
	}
}
