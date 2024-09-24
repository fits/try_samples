package main

import (
	"bufio"
	"context"
	"log"

	"github.com/libp2p/go-libp2p"
	"github.com/libp2p/go-libp2p/core/network"
)

func main() {
	node, err := libp2p.New(
		libp2p.ListenAddrStrings("/ip4/0.0.0.0/tcp/0"),
	)

	if err != nil {
		log.Fatal(err)
	}

	defer node.Close()

	node.SetStreamHandler("/sample", func(s network.Stream) {
		log.Println("called stream handler")

		msg, _ := readString(s)

		log.Printf("received: %s", msg)

		s.Write([]byte("ok\n"))

		s.Close()
	})

	log.Printf("started: peer=%s/p2p/%s", node.Addrs()[0], node.ID())

	ctx := context.Background()
	<-ctx.Done()
}

func readString(s network.Stream) (string, error) {
	buf := bufio.NewReader(s)
	return buf.ReadString('\n')
}
