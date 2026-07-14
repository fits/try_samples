package main

import (
	"fmt"
	"os"
)

func main() {
	fmt.Printf("called t1.go main args=%v\n", os.Args[1:])
}
