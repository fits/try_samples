package main

import (
    "os"
    "fmt"
    "strconv"
    "github.com/zeebe-io/zeebe/clients/go/zbc"
)

const BrokerAddr = "0.0.0.0:26500"

func main() {
    pid := os.Args[1]
    oid, _ := strconv.Atoi(os.Args[2])

    client, err := zbc.NewZBClient(&zbc.ZBClientConfig{
        GatewayAddress: BrokerAddr,
        UsePlaintextConnection: true,
    })

    if err != nil {
        panic(err)
    }

    v := make(map[string]interface{})
    v["order"] = oid

    req, err := client.NewCreateInstanceCommand().BPMNProcessId(pid).LatestVersion().VariablesFromMap(v)

    if err != nil {
        panic(err)
    }

    msg, err := req.Send()

    if err != nil {
        panic(err)
    }

    fmt.Println(msg.String())
}
