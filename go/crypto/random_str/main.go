package main

import (
	"crypto/rand"
	"encoding/hex"
	"fmt"
)

func main() {
	b := make([]byte, 5)
	_, err := rand.Read(b)

	if err != nil {
		fmt.Println(err)
		return
	}

	s := hex.EncodeToString(b)

	println(s)
}
