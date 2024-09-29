package main

import (
	"bufio"
	"context"
	"log"
	"os"
	"time"

	"github.com/libp2p/go-libp2p"
	"github.com/libp2p/go-libp2p/core/peer"
)

func main() {
	targetPeer := os.Args[1]

	node, err := libp2p.New(
		libp2p.ListenAddrStrings("/ip4/0.0.0.0/tcp/0"),
	)

	if err != nil {
		log.Fatal(err)
	}
	defer node.Close()

	info, err := peer.AddrInfoFromString(targetPeer)

	if err != nil {
		log.Fatal(err)
	}

	node.Connect(context.Background(), *info)

	s, err := node.NewStream(context.Background(), info.ID, "/clock")

	if err != nil {
		log.Fatal(err)
	}
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
