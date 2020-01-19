package main

import (
	"log"
	"os"
	"strconv"
	stan "github.com/nats-io/stan.go"
	"github.com/golang/protobuf/proto"
	pb "sample"
)

func GetenvOr(envName string, defaultValue string) string {
	res := os.Getenv(envName)

	if res != "" {
		return res
	}

	return defaultValue
}

func main() {
	clusterId := GetenvOr("NATS_CLUSTER_ID", "test-cluster")
	clientId := GetenvOr("NATS_CLIENT_ID", "sample-publisher")
	subject := GetenvOr("NATS_SUBJECT", "sample")

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
