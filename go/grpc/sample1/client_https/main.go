package main

import (
    "context"
    "crypto/tls"
    "log"
    "os"
    "time"

    "google.golang.org/grpc"
    "google.golang.org/grpc/credentials"
    pb "sample1"
)

func main() {
    address := "localhost:443"

    creds := credentials.NewTLS(&tls.Config{InsecureSkipVerify: true})

    con, err := grpc.Dial(address, grpc.WithTransportCredentials(creds))

    if err != nil {
        log.Fatalf("error: %v", err)
    }

    defer con.Close()

    c := pb.NewSampleServiceClient(con)

    msg := os.Args[1]

    ctx, cancel := context.WithTimeout(context.Background(), time.Second)
    defer cancel()

    r, err := c.Call(ctx, &pb.SampleRequest{Message: msg})

    if err != nil {
        log.Fatalf("failed: %v", err)
    }

    log.Println(r.Message)
}
