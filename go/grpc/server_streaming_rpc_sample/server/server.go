package main

import (
	"context"
	"log"
	"net"
	"sync"

	"github.com/google/uuid"

	"google.golang.org/grpc"
	pb "sample/proto/item"
)

const (
	address = ":50051"
)

var watches []chan<- *pb.Item
var mu sync.RWMutex

func watch(ch chan<- *pb.Item) {
	mu.Lock()
	watches = append(watches, ch)
	mu.Unlock()
}

func unwatch(ch chan<- *pb.Item) {
	var tmp []chan<- *pb.Item

	mu.Lock()
	defer mu.Unlock()

	for _, w := range watches {
		if w != ch {
			tmp = append(tmp, w)
		}
	}

	watches = tmp
}

func publish(item *pb.Item) {
	mu.RLock()
	defer mu.RUnlock()

	for _, w := range watches {
		w <- item
	}
}

type server struct {
	pb.UnimplementedItemManageServer
}

func (s *server) Create(ctx context.Context, req *pb.CreateItemRequest) (*pb.Item, error) {
	log.Printf("CREATE: %v\n", req)

	id, err := uuid.NewRandom()

	if err != nil {
		return nil, err
	}

	item := pb.Item{Id: id.String(), Value: req.GetValue()}

	go publish(&item)

	return &item, nil
}
func (s *server) Watch(req *pb.WatchItemRequest, stream pb.ItemManage_WatchServer) error {
	log.Printf("WATCH: %v\n", req)

	ch := make(chan *pb.Item)

	watch(ch)

	go func() {
		<- stream.Context().Done()
		log.Println("stream context done")

		unwatch(ch)
		close(ch)
	}()

	for {
		msg, ok := <-ch

		if !ok {
			log.Println("channel closed")
			return nil
		}

		stream.Send(msg)
	}
}

func main() {
	ln, err := net.Listen("tcp", address)

	if err != nil {
		log.Fatalf("failed listen: %v", err)
	}

	s := grpc.NewServer()
	pb.RegisterItemManageServer(s, &server{})

	log.Println("server start:", address)

	if err := s.Serve(ln); err != nil {
		log.Fatalf("failed serve: %v", err)
	}
}
