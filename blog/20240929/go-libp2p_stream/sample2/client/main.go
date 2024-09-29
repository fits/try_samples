package main

import (
	"bufio"
	"context"
	"log"
	"time"

	"github.com/libp2p/go-libp2p"
	"github.com/libp2p/go-libp2p/core/host"
	"github.com/libp2p/go-libp2p/core/network"
	"github.com/libp2p/go-libp2p/core/peer"
	"github.com/libp2p/go-libp2p/p2p/discovery/mdns"
)

type notifee struct {
	Node       host.Host
	StreamChan chan network.Stream
}

func (n *notifee) HandlePeerFound(info peer.AddrInfo) {
	log.Printf("peer found: id=%s, addrs=%v", info.ID, info.Addrs)

	if err := n.Node.Connect(context.Background(), info); err != nil {
		log.Printf("failed connect: %v", err)
		return
	}

	s, err := n.Node.NewStream(context.Background(), info.ID, "/clock")

	if err != nil {
		log.Printf("failed new stream: %v", err)
		return
	}

	n.StreamChan <- s
}

func main() {
	serviceName := "sample"

	node, err := libp2p.New(
		libp2p.ListenAddrStrings("/ip4/0.0.0.0/tcp/0"),
	)

	if err != nil {
		log.Fatal(err)
	}
	defer node.Close()

	n := notifee{node, make(chan network.Stream)}

	ds := mdns.NewMdnsService(node, serviceName, &n)

	if err = ds.Start(); err != nil {
		log.Fatal(err)
	}

	s := <-n.StreamChan
	defer s.Close()

	buf := bufio.NewReader(s)

	for _ = range 5 {
		_, err = s.Write([]byte("now\n"))

		if err != nil {
			log.Fatal(err)
		}

		res, _ := buf.ReadString('\n')

		log.Printf("now=%s", res)

		time.Sleep(3 * time.Second)
	}
}
