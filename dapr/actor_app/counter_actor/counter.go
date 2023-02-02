package main

import (
	"context"
	"log"
	"os"

	"github.com/dapr/go-sdk/actor"
	dapr "github.com/dapr/go-sdk/client"
	daprd "github.com/dapr/go-sdk/service/http"
)

func counterActorFactory() actor.Server {
	log.Print("called counterActorFactory")

	client, err := dapr.NewClient()

	if err != nil {
		log.Fatalf("failed to create client: %v", err)
	}

	return &CounterActor{daprClient: client}
}

type CounterActor struct {
	actor.ServerImplBase
	daprClient dapr.Client
}

func (a *CounterActor) Type() string {
	log.Print("called Type")
	return "counterActorType"
}

func (a *CounterActor) Counter(ctx context.Context) (int, error) {
	log.Print("called Counter")

	var counter int

	err := a.GetStateManager().Get("counter", &counter)

	return counter, err
}

func (a *CounterActor) CountUp(ctx context.Context) (int, error) {
	log.Print("called CountUp")

	counter, _ := a.Counter(ctx)

	counter += 1

	err := a.GetStateManager().Set("counter", counter)

	return counter, err
}

func main() {
	port := os.Getenv("APP_HTTP_PORT")

	if port == "" {
		port = "3000"
	}

	s := daprd.NewService(":" + port)

	s.RegisterActorImplFactory(counterActorFactory)

	err := s.Start()

	if err != nil {
		log.Fatalf("failed to start: %v", err)
	}
}
