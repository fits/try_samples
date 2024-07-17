package main

import (
	"context"
	"fmt"
	"log"
	"os"
	"time"

	"github.com/grandcat/zeroconf"
)

func main() {
	service := os.Args[1]

	fmt.Printf("resolve name: %s \n", service)

	rsl, err := zeroconf.NewResolver(nil)

	if err != nil {
		log.Fatal(err)
	}

	ch := make(chan *zeroconf.ServiceEntry)

	ctx, cancel := context.WithTimeout(context.Background(), time.Second*30)
	defer cancel()

	go func(rs <-chan *zeroconf.ServiceEntry) {
		for r := range rs {
			fmt.Printf("ServiceEntry: hostname=%s, port=%d, domain=%s, service=%s, instance=%s, ipv4=%v, ipv6=%v \n",
				r.HostName, r.Port, r.Domain, r.Service, r.ServiceInstanceName(), r.AddrIPv4, r.AddrIPv6)

			cancel()
		}
	}(ch)

	err = rsl.Browse(ctx, service, "local.", ch)

	if err != nil {
		log.Fatal(err)
	}

	<-ctx.Done()
}
