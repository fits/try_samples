package main

import (
	"context"
	"log"

	"github.com/containerd/containerd"
	"github.com/containerd/containerd/events"
	"github.com/containerd/containerd/namespaces"
	"github.com/containerd/typeurl"
	_ "github.com/containerd/containerd/api/events"
)

const (
	address = "/var/snap/docker/current/run/docker/containerd/containerd.sock"
)

func printEnvelope(env *events.Envelope) {
	event, err := typeurl.UnmarshalAny(env.Event)

	if err != nil {
		log.Printf("ERROR %v", err)
	}

	log.Printf(
		"topic = %s, namespace = %s, event.typeurl = %s, event = %v", 
		env.Topic, env.Namespace, env.Event.TypeUrl, event,
	)
}

func main() {
	client, err := containerd.New(address)

	if err != nil {
		log.Fatal(err)
	}

	defer client.Close()

	ctx := context.Background()

	ns, err := client.NamespaceService().List(ctx)

	if err != nil {
		log.Printf("ERROR %v", err)
		return
	}

	ctx = namespaces.WithNamespace(ctx, ns[0])

	ch, errs := client.Subscribe(ctx)

	for {
		select {
		case env := <-ch:
			printEnvelope(env)
		case e := <-errs:
			log.Printf("ERROR %v", e)
			return
		}
	}
}