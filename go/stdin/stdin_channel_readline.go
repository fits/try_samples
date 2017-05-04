package main

import (
	"fmt"
	"bufio"
	"strings"
	"os"
)

func main() {
	ch := make(chan string)
	reader := bufio.NewReader(os.Stdin)

	go func() {
		for {
			b, _ := reader.ReadString('\n')
			ch <- strings.TrimSpace(b)
		}
	}()

	loop:
	for {
		select {
			case s := <- ch:
				if s == "q" {
					break loop
				}

				fmt.Println("input = ", s)
		}
	}
}
