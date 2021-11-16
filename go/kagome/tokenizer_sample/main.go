package main

import (
	"fmt"
	"os"

	"github.com/ikawaha/kagome-dict/ipa"
	"github.com/ikawaha/kagome/v2/tokenizer"
)

func main() {
	text := os.Args[1]

	t, err := tokenizer.New(ipa.Dict(), tokenizer.OmitBosEos())

	if err != nil {
		panic(err)
	}

	tokens := t.Tokenize(text)

	for _, token := range tokens {
		fmt.Printf("surface: %s, features: %v\n", token.Surface, token.Features())
	}
}
