package main

import (
	"fmt"
	"bufio"
	"os"
)

func main() {
	reader := bufio.NewReader(os.Stdin)

	b, _ := reader.ReadString('\n')

	fmt.Println("input = ", b)
}
