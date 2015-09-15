package main

import (
	"fmt"
	"os"
	"regexp"
)

func main() {
	r := regexp.MustCompile(`[#:!?](.*)$`)

	fmt.Println(r.FindStringSubmatch(os.Args[1]))
}
