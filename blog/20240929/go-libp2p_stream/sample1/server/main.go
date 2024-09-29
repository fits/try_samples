package main

import (
	"bufio"
	"context"
	"fmt"
	"log"
	"os"
	"os/signal"
	"time"

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

	addr := fmt.Sprintf("%s/p2p/%s", node.Addrs()[0], node.ID())

	log.Printf("started: peer=%s", addr)

	awaitTerminate()
}

func awaitTerminate() {
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
