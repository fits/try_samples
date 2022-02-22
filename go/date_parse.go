package main

import (
	"fmt"
	"time"
)

func main() {
	fmt.Println(time.RFC3339)
	
	t, _ := time.Parse(time.RFC3339, "2022-02-23T01:20:35+09:00")
	
	fmt.Println(t)
}
