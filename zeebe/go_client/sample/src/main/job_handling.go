package main

import (
    "os"
    "fmt"
    "github.com/zeebe-io/zeebe/clients/go/entities"
    "github.com/zeebe-io/zeebe/clients/go/worker"
    "github.com/zeebe-io/zeebe/clients/go/zbc"
)

const BrokerAddr = "0.0.0.0:26500"

func main() {
    jobType := os.Args[1]

    client, err := zbc.NewZBClient(&zbc.ZBClientConfig{
        GatewayAddress: BrokerAddr,
        UsePlaintextConnection: true,
    })

    if err != nil {
        panic(err)
    }

    jobWorker := client.NewJobWorker().JobType(jobType).Handler(handleJob).Open()

    defer jobWorker.Close()

    jobWorker.AwaitClose()
}

func handleJob(client worker.JobClient, job entities.Job) {
    jobKey := job.GetKey()

    fmt.Println("jobkey:", jobKey)

    v, _ := job.GetVariablesAsMap()

    fmt.Println("variables:", v)

    req, err := client.NewCompleteJobCommand().JobKey(jobKey).VariablesFromMap(v)

    if err != nil {
        fmt.Println("Failed to complete job:", jobKey)

        client.NewFailJobCommand().JobKey(jobKey).Retries(job.Retries - 1).Send()

        return
    }

    req.Send()
}
