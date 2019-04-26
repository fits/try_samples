package main

import (
	"context"
	"net"
	"log"
	"google.golang.org/grpc"
	pb "sample1"
)

type server struct {}

func (s *server) Call(ctx context.Context, in *pb.SampleRequest) (*pb.SampleResponse, error) {
	return &pb.SampleResponse { Message: in.GetMessage() + "!!!" }, nil
}

func main() {
	port := ":50051"

	lis, err := net.Listen("tcp", port)

	if err != nil {
		log.Fatalf("error: %v", err)
	}

	s := grpc.NewServer()

	pb.RegisterSampleServiceServer(s, &server{})

	log.Println("server start")

	if err := s.Serve(lis); err != nil {
		log.Fatalf("failed serve: %v", err)
	}

}
