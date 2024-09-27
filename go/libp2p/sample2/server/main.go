package main

import (
	"bufio"
	"context"
	"log"
	"os"
	"os/signal"
	"time"

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
	serviceName := "sample2"

	node, err := libp2p.New(
		libp2p.ListenAddrStrings("/ip4/0.0.0.0/tcp/0"),
	)

	if err != nil {
		log.Fatal(err)
	}
	defer node.Close()

	node.SetStreamHandler("/clock", func(s network.Stream) {
		defer s.Close()

		log.Println("called stream handler")

		buf := bufio.NewReader(s)

		for {
			msg, err := buf.ReadString('\n')

			if err != nil {
				log.Printf("failed read stream: %v", err)
				s.Reset()
				break
			}

			t := ""

			if msg == "now\n" {
				t = time.Now().Format(time.RFC3339)
			}

			s.Write([]byte(t + "\n"))
		}
	})

	ds := mdns.NewMdnsService(node, serviceName, &notifee{})

	if err = ds.Start(); err != nil {
		log.Fatal(err)
	}

	log.Printf("started: id=%s", node.ID())

	awaitTerm()
}

func awaitTerm() {
	ctx, cancel := context.WithCancel(context.Background())

	go func() {
		sig := make(chan os.Signal, 1)
		signal.Notify(sig, os.Interrupt)

		<-sig

		log.Print("stop")

		cancel()
	}()

	<-ctx.Done()
}
