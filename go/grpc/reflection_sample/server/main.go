package main

import (
    "context"
    "fmt"
    "net"
    "log"
    "google.golang.org/grpc"
    "google.golang.org/grpc/reflection"
    empty "github.com/golang/protobuf/ptypes/empty"
    pb "sample/proto/item"
)

type Server struct {
    Items map[string]pb.Item
}

func (s *Server) GetItem(ctx context.Context, req *pb.ItemRequest) (*pb.Item, error) {
    log.Println("call GetItem: ", req)

    item, ok := s.Items[req.GetItemId()]

    if !ok {
        return nil, fmt.Errorf("item not found: %s", req.GetItemId())
    }

    return &item, nil
}

func (s *Server) AddItem(ctx context.Context, req *pb.AddItemRequest) (*empty.Empty, error) {
    log.Println("call AddItem: ", req)

    s.Items[req.GetItemId()] = pb.Item{ItemId: req.GetItemId(), Price: req.GetPrice()}

    return &empty.Empty{}, nil
}

func main() {
    address := ":50051"

    listen, err := net.Listen("tcp", address)

    if err != nil {
        log.Fatalf("error: %v", err)
    }

    s := grpc.NewServer()

    pb.RegisterItemServiceServer(s, &Server{Items: make(map[string]pb.Item)})
    reflection.Register(s)

    log.Println("server start:", address)

    if err := s.Serve(listen); err != nil {
        log.Fatalf("failed serve: %v", err)
    }
}
