package main

import (
	"context"
	"log"

	"github.com/containerd/containerd"
	"github.com/containerd/containerd/namespaces"
)

const (
	address = "/var/snap/docker/current/run/docker/containerd/containerd.sock"
)

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

	log.Printf("%#v", ns)

	ctx = namespaces.WithNamespace(ctx, ns[0])

	cs, err := client.Containers(ctx)

	if err != nil {
		log.Printf("ERROR %v", err)
		return
	}

	log.Printf("%#v", cs)

	for _, c := range cs {
		log.Printf("%#v", c)
	}
}
