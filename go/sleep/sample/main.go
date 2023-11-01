package main

import (
	"fmt"
	"math/rand"
	"time"
)

func main() {
	t := rand.Intn(3000)

	fmt.Printf("sleep: %d ms\n", t)

	time.Sleep(time.Duration(t) * time.Millisecond)

	fmt.Println("end")
}
