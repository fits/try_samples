package main

import (
	"context"
	"fmt"
	"log"
	"os"
	"os/signal"
	"syscall"

	"github.com/containerd/containerd"
	"github.com/containerd/containerd/events"
	"github.com/containerd/typeurl"
	apievents "github.com/containerd/containerd/api/events"
)

const (
	address = "/var/snap/docker/current/run/docker/containerd/containerd.sock"
	namespace = "moby"
)

func printEnvelope(env *events.Envelope) {
	event, err := typeurl.UnmarshalAny(env.Event)

	if err != nil {
		log.Printf("ERROR unmarshal %v", err)
	}

	var s string

	switch ev := event.(type) {
	case *apievents.ContainerCreate:
		s = fmt.Sprintf("{ id = %s, image = %s }", ev.ID, ev.Image)
	case *apievents.ContainerDelete:
		s = fmt.Sprintf("{ id = %s }", ev.ID)
	case *apievents.TaskCreate:
		s = fmt.Sprintf(
			"{ container_id = %s, pid = %d, bundle = %s }", 
			ev.ContainerID, ev.Pid, ev.Bundle,
		)
	case *apievents.TaskStart:
		s = fmt.Sprintf(
			"{ container_id = %s, pid = %d }", 
			ev.ContainerID, ev.Pid,
		)
	case *apievents.TaskExit:
		s = fmt.Sprintf(
			"{ container_id = %s, pid = %d, exit_status = %d }", 
			ev.ContainerID, ev.Pid, ev.ExitStatus,
		)
	case *apievents.TaskDelete:
		s = fmt.Sprintf(
			"{ container_id = %s, pid = %d, exit_status = %d }", 
			ev.ContainerID, ev.Pid, ev.ExitStatus,
		)
	}

	log.Printf(
		"topic = %s, namespace = %s, event.typeurl = %s, event = %v", 
		env.Topic, env.Namespace, env.Event.TypeUrl, s,
	)
}

func main() {
	client, err := containerd.New(
		address, 
		containerd.WithDefaultNamespace(namespace),
	)

	if err != nil {
		log.Fatal(err)
	}

	//defer client.Close()
	defer func() {
		log.Print("close")
		client.Close()
	}()

	sig := make(chan os.Signal, 1)
	signal.Notify(sig, syscall.SIGINT, syscall.SIGTERM)

	ctx, cancel := context.WithCancel(context.Background())

	go func() {
		s := <-sig
		log.Printf("syscall: %v", s)
		cancel()
	}()

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