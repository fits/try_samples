package main

import (
	"bufio"
	"fmt"
	"os"

	stan "github.com/nats-io/stan.go"
)

func main() {
	clusterId := "test-cluster"
	clientId := os.Args[1]
	subject := os.Args[2]

	sc, _ := stan.Connect(clusterId, clientId)

	sub, _ := sc.Subscribe(subject, func(m *stan.Msg) {
		fmt.Printf("seq=%d, data=%s\n", m.Sequence, string(m.Data))
	})

	reader := bufio.NewReader(os.Stdin)
	reader.ReadString('\n')

	sub.Unsubscribe()
	sc.Close()
}
