package parser

import "unicode"

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

func (p *Parser) ParseToken(t string) bool {
	p.skipSpace()

	rs := []rune(t)

	if len(rs) > 0 && p.pos+len(rs) <= len(p.runes) {
		for i, r := range rs {
			if r != p.runes[p.pos+i] {
				return false
			}
		}

		p.last_token = t
		p.pos += len(rs)

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
