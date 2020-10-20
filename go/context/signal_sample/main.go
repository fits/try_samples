package main

import (
	"context"
	"fmt"
	"os"
	"os/signal"
	"syscall"
	"time"
)

func main() {
	ctx := context.Background()
	ctx, cancel := context.WithTimeout(ctx, 5*time.Second)

	sig := make(chan os.Signal)
	signal.Notify(sig, syscall.SIGINT, syscall.SIGTERM)

	go func() {
		<-sig
		fmt.Println("* received signal")
		cancel()
	}()

	for {
		fmt.Println("wait timeout")

		select {
		case <-ctx.Done():
			fmt.Println("done")
			return
		}
	}
}
