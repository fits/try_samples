package main

import (
	"bufio"
	"fmt"
	"log"
	"os"

	stan "github.com/nats-io/stan.go"
	"github.com/golang/protobuf/proto"

	pb "sample"
)

func main() {
	clusterId := "test-cluster"
	clientId := os.Args[1]
	subject := "sample-protobuf"

	sc, _ := stan.Connect(clusterId, clientId)

	sub, _ := sc.Subscribe(subject, func(m *stan.Msg) {
		data := &pb.DataEvent{}

		err := proto.Unmarshal(m.Data, data)

		if err != nil {
			log.Fatal("failed unmarshal: ", err)
		}

		fmt.Printf("seq=%d, data=%s\n", m.Sequence, data)
	})

	reader := bufio.NewReader(os.Stdin)

	reader.ReadString('\n')

	sub.Unsubscribe()
	sc.Close()
}
