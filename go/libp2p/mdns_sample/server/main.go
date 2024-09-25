package main

import (
	"bufio"
	"context"
	"log"
	"os"
	"os/signal"

	"github.com/libp2p/go-libp2p"
	"github.com/libp2p/go-libp2p/core/network"
	"github.com/libp2p/go-libp2p/core/peer"
	"github.com/libp2p/go-libp2p/p2p/discovery/mdns"
)

type notifee struct{}

func (n *notifee) HandlePeerFound(info peer.AddrInfo) {
	log.Printf("peer found: id=%s, addrs=%v", info.ID, info.Addrs)
}

func main() {
	serviceName := "svc1"

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

	ds := mdns.NewMdnsService(node, serviceName, &notifee{})

	if err = ds.Start(); err != nil {
		log.Fatal(err)
	}

	ctx, cancel := context.WithCancel(context.Background())

	go func() {
		sig := make(chan os.Signal, 1)
		signal.Notify(sig, os.Interrupt)

		<-sig

		log.Print("stop")

		cancel()
	}()

	log.Printf("started: id=%s", node.ID())

	<-ctx.Done()
}

func readString(s network.Stream) (string, error) {
	buf := bufio.NewReader(s)
	return buf.ReadString('\n')
}
