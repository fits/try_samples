package main

import (
	"context"
	"fmt"
	"path/filepath"
	"log"
	"os"
	"strings"

	"github.com/containerd/containerd"
	"github.com/containerd/containerd/namespaces"
)

func main() {
	pid := containerdPid()
	address := filepath.Join("/proc", pid, "root/run/containerd/containerd.sock")

	client, err := containerd.New(address)

	if err != nil {
		log.Fatal(err)
	}

	defer client.Close()

	ctx := context.Background()

	ns, err := client.NamespaceService().List(ctx)

	if err != nil {
		log.Fatal(err)
	}

	fmt.Printf("namespaces: %v\n", ns)

	ctx = namespaces.WithNamespace(ctx, ns[0])

	cs, err := client.Containers(ctx)

	if err != nil {
		log.Fatal(err)
	}

	for _, c := range cs {
		img, _ := c.Image(ctx)
		fmt.Printf("container: id=%s, image=%s\n", c.ID(), img.Name())
	}
}

func containerdPid() string {
	xdrDir := os.Getenv("XDG_RUNTIME_DIR")

	file := filepath.Join(xdrDir, "containerd-rootless/child_pid")

	pid, _ := os.ReadFile(file)

	return strings.TrimSpace(string(pid))
}
