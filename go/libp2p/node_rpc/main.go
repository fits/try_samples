package main

import (
	"bufio"
	"context"
	"fmt"
	"io"
	"log"
	"net/http"
	"os"

	"github.com/libp2p/go-libp2p"
	"github.com/libp2p/go-libp2p/core/host"
	"github.com/libp2p/go-libp2p/core/network"
	"github.com/libp2p/go-libp2p/core/peer"
	"github.com/libp2p/go-libp2p/p2p/discovery/mdns"
)

const requestProtocolId = "/request"
const responseProtocolId = "/response"

type notifee struct {
	peerChan chan peer.AddrInfo
}

func (n *notifee) HandlePeerFound(info peer.AddrInfo) {
	log.Printf("peer found: id=%s, addrs=%v", info.ID, info.Addrs)
	n.peerChan <- info
}

type Node struct {
	host.Host
	name string
}

func (n *Node) OnRequest(s network.Stream) {
	log.Printf("handle request: id=%s, remote=%s", s.ID(), s.Conn().RemotePeer())

	msg, _ := readString(s)
	s.Close()

	log.Printf("received request: %s", msg)

	rs, err := n.Host.NewStream(context.Background(), s.Conn().RemotePeer(), responseProtocolId)

	if err != nil {
		log.Println(err)
		return
	}
	defer rs.Close()

	w := bufio.NewWriter(rs)

	_, err = w.WriteString(fmt.Sprintf("peer=%s, name=%s\n", n.Host.ID(), n.name))

	if err != nil {
		log.Println(err)
		return
	}

	err = w.Flush()

	if err != nil {
		log.Println(err)
	}

	log.Printf("returned response to %s", s.Conn().RemotePeer())
}

func (n *Node) OnResponse(s network.Stream) {
	log.Printf("handle response: id=%s, remote=%s", s.ID(), s.Conn().RemotePeer())

	msg, _ := readString(s)
	s.Close()

	log.Printf("received response: %s", msg)
}

func (n *Node) Close() {
	n.Host.Close()
}

func (n *Node) ConnectPeer(ctx context.Context, addr peer.AddrInfo) error {
	return n.Host.Connect(ctx, addr)
}

func (n *Node) SendRequest(msg string) {
	log.Printf("send request: msg=%s", msg)

	ctx := context.Background()

	for _, c := range n.Host.Network().Conns() {
		if len(c.GetStreams()) == 0 {
			s, err := n.Host.NewStream(ctx, c.RemotePeer(), requestProtocolId)

			if err != nil {
				log.Printf("failed new stream: remote=%s, error=%s", c.RemotePeer(), err)
				continue
			}
			defer s.Close()

			w := bufio.NewWriter(s)
			_, err = w.WriteString(fmt.Sprintf("%s\n", msg))

			if err != nil {
				log.Printf("failed write stream: remote=%s, error=%s", c.RemotePeer(), err)
				continue
			}

			w.Flush()
		}
	}
}

func NewNode(name string) (*Node, error) {
	h, err := libp2p.New(
		libp2p.ListenAddrStrings("/ip4/0.0.0.0/tcp/0"),
	)

	if err != nil {
		return nil, err
	}

	node := Node{h, name}

	node.SetStreamHandler(requestProtocolId, node.OnRequest)
	node.SetStreamHandler(responseProtocolId, node.OnResponse)

	return &node, nil
}

func readString(s network.Stream) (string, error) {
	buf := bufio.NewReader(s)
	return buf.ReadString('\n')
}

func main() {
	httpPort := os.Args[1]
	group := os.Args[2]
	name := os.Args[3]

	node, err := NewNode(name)

	if err != nil {
		log.Fatal(err)
	}
	defer node.Close()

	n := notifee{peerChan: make(chan peer.AddrInfo)}

	ds := mdns.NewMdnsService(node.Host, group, &n)

	if err = ds.Start(); err != nil {
		log.Fatal(err)
	}

	go func() {
		ctx := context.Background()
		for {
			peer := <-n.peerChan

			err = node.ConnectPeer(ctx, peer)

			if err != nil {
				log.Println(err)
				continue
			}

			log.Printf("connected: %s", peer)
		}
	}()

	log.Printf("started: id=%s, name=%s, http-port=%s", node.Host.ID(), node.name, httpPort)

	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		buf, _ := io.ReadAll(r.Body)
		msg := string(buf)

		go node.SendRequest(msg)

		fmt.Fprintf(w, "ok")
	})

	log.Fatal(http.ListenAndServe(":"+httpPort, nil))
}
