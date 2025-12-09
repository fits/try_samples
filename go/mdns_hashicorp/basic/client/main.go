package main

import (
	"log"

	"github.com/hashicorp/mdns"
)

func main() {
	serviceName := "_testservice._tcp"

	ch := make(chan *mdns.ServiceEntry)

	go func() {
		for p := range ch {
			log.Printf("detected: %v", p)
		}
	}()

	mdns.Lookup(serviceName, ch)
	close(ch)
}
