package main

import (
	"os"
	"net"
	"log"
	"google.golang.org/grpc"
	stan "github.com/nats-io/stan.go"
	"github.com/golang/protobuf/proto"
	pb "sample"
)

type server struct {
	ClusterId string
	Subject string
}

func (s *server) Subscribe(req *pb.SubscribeRequest, stream pb.EventNotifyService_SubscribeServer) error {

	sc, err := stan.Connect(s.ClusterId, req.ClientId)

	if err != nil {
		return err
	}

	defer sc.Close()

	log.Println("connected: clientId=", req.ClientId, ", durableName=", req.DurableName)

	messages := make(chan *stan.Msg)

	_, err = sc.Subscribe(s.Subject, func(m *stan.Msg) {
		log.Println("received message from nats: seq=", m.Sequence)

		go func() {
			messages <- m
		}()

	}, stan.DurableName(req.DurableName), stan.SetManualAckMode())

	if err != nil {
		return err
	}

	for {
		msg := <- messages

		event := &pb.DataEvent{}
		err := proto.Unmarshal(msg.Data, event)

		if err != nil {
			log.Println(err)
		} else {
			err = stream.Send(event)

			if err != nil {
				log.Println("disconnected: clientId=", req.ClientId)
				return err
			}

			msg.Ack()
		}
	}
}

func GetenvOr(envName string, defaultValue string) string {
	res := os.Getenv(envName)

	if res != "" {
		return res
	}

	return defaultValue
}

func main() {
	port := GetenvOr("SERVICE_PORT", "50051")
	clusterId := GetenvOr("NATS_CLUSTER_ID", "test-cluster")
	subject := GetenvOr("NATS_SUBJECT", "sample")

	listen, err := net.Listen("tcp", ":" + port)

	if err != nil {
		log.Fatalf("error: %v", err)
	}

	s := grpc.NewServer()

	pb.RegisterEventNotifyServiceServer(s, &server{ClusterId: clusterId, Subject: subject})

	log.Println("server started: ", port)

	if err := s.Serve(listen); err != nil {
		log.Fatalf("failed serve: %v", err)
	}
}
