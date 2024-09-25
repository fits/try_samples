package main

import (
	"bufio"
	"context"
	"log"
	"os"

	"github.com/libp2p/go-libp2p"
	"github.com/libp2p/go-libp2p/core/network"
	"github.com/libp2p/go-libp2p/core/peer"
	"github.com/libp2p/go-libp2p/p2p/discovery/mdns"
)

type notifee struct {
	PeerChan chan peer.AddrInfo
}

func (n *notifee) HandlePeerFound(info peer.AddrInfo) {
	log.Printf("peer found: id=%s, addrs=%v", info.ID, info.Addrs)
	n.PeerChan <- info
}

func main() {
	serviceName := "svc1"
	msg := os.Args[1]

	node, err := libp2p.New(
		libp2p.ListenAddrStrings("/ip4/0.0.0.0/tcp/0"),
	)

	if err != nil {
		log.Fatal(err)
	}
	defer node.Close()

	n := notifee{make(chan peer.AddrInfo)}

	ds := mdns.NewMdnsService(node, serviceName, &n)

	if err = ds.Start(); err != nil {
		log.Fatal(err)
	}

	info := <-n.PeerChan

	if err = node.Connect(context.Background(), info); err != nil {
		log.Fatal(err)
	}

	s, err := node.NewStream(context.Background(), info.ID, "/sample")

	if err != nil {
		log.Fatal(err)
	}
	defer s.Close()

	_, err = s.Write([]byte(msg + "\n"))

	if err != nil {
		log.Fatal(err)
	}

	res, _ := readString(s)

	log.Printf("result: %s", res)
}

func readString(s network.Stream) (string, error) {
	buf := bufio.NewReader(s)
	return buf.ReadString('\n')
}
