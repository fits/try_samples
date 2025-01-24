package main

import (
	"bufio"
	"fmt"
	"os"
)

func main() {
	s := bufio.NewScanner(os.Stdin)

	for s.Scan() {
		t := s.Text()

		if len(t) > 0 {
			fmt.Printf("* input=%s\n", t)
		} else {
			break
		}
	}
}
