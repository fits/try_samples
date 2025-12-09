package main

import (
	"context"
	"log"
	"net"

	"github.com/hashicorp/mdns"
)

func main() {
	instanceName := "test1"
	serviceName := "_testservice._tcp"
	hostName := "test1.example.com."
	ips := []net.IP{net.IPv4(192, 168, 1, 1)}
	info := []string{"test service", "id=t1"}

	service, err := mdns.NewMDNSService(instanceName, serviceName, "", hostName, 8080, ips, info)

	if err != nil {
		log.Fatal(err)
	}

	log.Print(service)

	server, err := mdns.NewServer(&mdns.Config{Zone: service})

	if err != nil {
		log.Fatal(err)
	}
	defer server.Shutdown()

	ctx := context.Background()
	<-ctx.Done()
}
