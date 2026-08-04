package main

import (
	"fmt"
	"os"
	"strings"
	"unicode"
)

type Parser struct {
	runes      []rune
	pos        int
	last_token string
}

func NewParser(s string) Parser {
	return Parser{runes: []rune(s)}
}

func (p *Parser) IsEos() bool {
	return !p.IsNotEos()
}

func (p *Parser) IsNotEos() bool {
	return p.pos < len(p.runes)
}

func (p *Parser) LastToken() string {
	return p.last_token
}

func (p *Parser) ParseIdent() bool {
	p.skipSpace()

	s := ""

	for p.IsNotEos() && isIdentRune(p.runes[p.pos]) {
		s += string(p.runes[p.pos])
		p.pos += 1
	}

	if len(s) > 0 {
		p.last_token = s
		return true
	}

	return false
}

func (p *Parser) ParseRune(r rune) bool {
	p.skipSpace()

	if p.IsNotEos() && r == p.runes[p.pos] {
		p.last_token = string(r)
		p.pos += 1
		return true
	}

	return false
}

func (p *Parser) skipSpace() {
	for p.IsNotEos() && unicode.IsSpace(p.runes[p.pos]) {
		p.pos += 1
	}
}

func isAlphaNumeric(r rune) bool {
	return ('a' <= r && r <= 'z') || ('A' <= r && r <= 'Z') || ('0' <= r && r <= '9')
}

func isIdentRune(r rune) bool {
	return r == '_' || isAlphaNumeric(r)
}

type AdtType struct {
	ty       string
	elements []string
}

func parse(g string) (*AdtType, error) {
	p := NewParser(g)

	if !p.ParseIdent() {
		return nil, fmt.Errorf("syntax error")
	}

	ty := p.LastToken()

	if !p.ParseRune('=') || !p.ParseIdent() {
		return nil, fmt.Errorf("syntax error")
	}

	els := []string{p.LastToken()}

	for p.ParseRune('|') && p.ParseIdent() {
		els = append(els, p.LastToken())
	}

	if len(els) != 2 {
		return nil, fmt.Errorf("syntax error")
	}

	return &AdtType{ty, els}, nil
}

func generateCode(t *AdtType, pckg string, fileName string) error {
	methodName := fmt.Sprintf("Is%s", t.ty)

	body := fmt.Sprintf("package %s \n", pckg)
	body += fmt.Sprintf("type %s interface { %s() } \n", t.ty, methodName)

	for _, el := range t.elements {
		body += fmt.Sprintf("func (%s) %s() {} \n", el, methodName)
	}

	return os.WriteFile(fileName, []byte(body), 0644)
}

func main() {
	arg := os.Args[1]

	t, err := parse(arg)

	if err != nil {
		fmt.Fprintf(os.Stderr, "syntax error: %s \n", arg)
		os.Exit(1)
	}

	fileName := fmt.Sprintf("gen_%s_%s", strings.ToLower(t.ty), os.Getenv("GOFILE"))

	err = generateCode(t, os.Getenv("GOPACKAGE"), fileName)

	if err != nil {
		fmt.Fprintf(os.Stderr, "failed generate file: %v \n", err)
		os.Exit(1)
	}
}
