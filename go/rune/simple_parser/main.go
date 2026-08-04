package main

import (
	"fmt"
	"simple_parser/parser"
)

func main() {
	p := parser.NewParser(" AbC_ = A1 | B2 with C_3 ")

	r := p.ParseIdent()

	fmt.Printf("result=%t, token='%s' \n", r, p.LastToken())
	fmt.Printf("parseIdent result=%t \n", p.ParseIdent())

	r = p.ParseRune('=')
	fmt.Printf("result=%t, token='%s' \n", r, p.LastToken())
	fmt.Printf("parseRune result=%t, token='%s' \n", p.ParseRune('='), p.LastToken())

	r = p.ParseIdent()
	fmt.Printf("result=%t, token='%s' \n", r, p.LastToken())

	r = p.ParseRune('|')
	fmt.Printf("result=%t, token='%s' \n", r, p.LastToken())

	r = p.ParseIdent()
	fmt.Printf("result=%t, token='%s' \n", r, p.LastToken())

	r = p.ParseToken("with")
	fmt.Printf("result=%t, token='%s' \n", r, p.LastToken())

	r = p.ParseIdent()
	fmt.Printf("result=%t, token='%s' \n", r, p.LastToken())

	fmt.Printf("eos=%t \n", p.IsEos())

	r = p.ParseIdent()
	fmt.Printf("result=%t, token='%s' \n", r, p.LastToken())

	fmt.Printf("eos=%t \n", p.IsEos())
}
