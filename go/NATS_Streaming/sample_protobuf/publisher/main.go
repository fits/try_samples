package main

import (
	"log"
	"os"
	"strconv"

	stan "github.com/nats-io/stan.go"
	"github.com/golang/protobuf/proto"

	pb "sample"
)

func main() {
	clusterId := "test-cluster"
	clientId := "gp1"
	subject := "sample-protobuf"
	eventId := os.Args[1]
	dataId := os.Args[2]

	var data *pb.DataEvent

	if len(os.Args) <= 3 {
		crt := &pb.Created{ DataId: dataId }
		ev := &pb.DataEvent_Created{ Created: crt }

		data = &pb.DataEvent{ EventId: eventId, Event: ev }
	} else {
		v, _ := strconv.Atoi(os.Args[3])
		upd := &pb.Updated{ DataId: dataId, Value: int32(v) }
		ev := &pb.DataEvent_Updated{ Updated: upd }

		data = &pb.DataEvent{ EventId: eventId, Event: ev }
	}

	buf, err := proto.Marshal(data)

	if err != nil {
		log.Fatal("marshal failed: ", err)
	}

	sc, _ := stan.Connect(clusterId, clientId)
	defer sc.Close()

	sc.Publish(subject, buf)
}
