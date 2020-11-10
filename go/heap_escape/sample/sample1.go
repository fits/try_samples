// go 1.15
// go build -gcflags="-m" sample1.go

package main

import (
	"fmt"
	"log"
)
func main() {
	println("a-1")
	fmt.Println("b-2") // "b-2" escapes to heap
	fmt.Println(33, "c-3") // 33 escapes to heap, "c-3" escapes to heap
	log.Println("d-4") // "d-4" escapes to heap
}
