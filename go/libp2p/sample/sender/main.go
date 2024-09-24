package main

import (
	"bufio"
	"context"
	"log"
	"os"

	"github.com/libp2p/go-libp2p"
	"github.com/libp2p/go-libp2p/core/network"
	"github.com/libp2p/go-libp2p/core/peer"
)

func main() {
	targetPeer := os.Args[1]
	msg := os.Args[2]

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

	s, err := node.NewStream(context.Background(), info.ID, "/sample")

	if err != nil {
		log.Fatal(err)
	}
	defer s.Close()

	log.Print("success new stream")

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
