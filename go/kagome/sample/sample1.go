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

	ts := t.Tokenize(text)

	for _, t := range ts {
		fmt.Printf("term=%s, partOfSpeech=%v\n", t.Surface, t.POS())
	}
}
