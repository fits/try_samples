package main

import (
    "context"
    "log"
    "os"
    "strconv"
    "time"
    "google.golang.org/grpc"
    pb "sample/proto/item"
)

func main() {
    address := "localhost:50051"

    con, err := grpc.Dial(address, grpc.WithInsecure())

    if err != nil {
        log.Fatalf("error: %v", err)
    }

    defer con.Close()

    client := pb.NewItemServiceClient(con)

    ctx, cancel := context.WithTimeout(context.Background(), time.Second)
    defer cancel()

    itemId := os.Args[1]
    price, err := strconv.Atoi(os.Args[2])

    if err != nil {
        log.Fatalf("not integer: %v", os.Args[2])
    }

    _, err = client.AddItem(ctx, &pb.AddItemRequest{ItemId: itemId, Price: int32(price)})

    if err != nil {
        log.Fatalf("failed: %v", err)
    }

    item, _ := client.GetItem(ctx, &pb.ItemRequest{ItemId: itemId})

    log.Println("item: ", item)
}
