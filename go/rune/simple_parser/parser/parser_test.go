package parser_test

import (
	"simple_parser/parser"
	"testing"
)

func TestParseIdent(t *testing.T) {
	p := parser.NewParser("A1_ ")

	if p.ParseIdent() {
		if p.LastToken() != "A1_" {
			t.Errorf("unmatch: %s", p.LastToken())
		}

	} else {
		t.Error("failed parse")
	}
}

func TestParseIdentBeginSpace(t *testing.T) {
	p := parser.NewParser("   B_2_q  ")

	if p.ParseIdent() {
		if p.LastToken() != "B_2_q" {
			t.Errorf("unmatch: %s", p.LastToken())
		}

	} else {
		t.Error("failed parse")
	}
}

func TestParseRune(t *testing.T) {
	p := parser.NewParser("=A")

	if p.ParseRune('=') {
		if p.LastToken() != "=" {
			t.Errorf("unmatch: %s", p.LastToken())
		}
	} else {
		t.Error("failed parse")
	}
}

func TestParseRuneBeginIdent(t *testing.T) {
	p := parser.NewParser("A/B")

	if p.ParseIdent() && p.ParseRune('/') {
		if p.LastToken() != "/" {
			t.Errorf("unmatch: %s", p.LastToken())
		}
	} else {
		t.Error("failed parse")
	}
}

func TestParseToken(t *testing.T) {
	p := parser.NewParser("A with")

	if p.ParseIdent() && p.ParseToken("with") {
		if p.LastToken() != "with" {
			t.Errorf("unmatch: %s", p.LastToken())
		}

		if !p.IsEos() {
			t.Error("not eos")
		}
	} else {
		t.Error("failed parse")
	}
}

func TestParseTokenIsNext(t *testing.T) {
	p := parser.NewParser("A with B")

	if p.ParseIdent() && p.ParseToken("with") {
		if p.LastToken() != "with" {
			t.Errorf("unmatch: %s", p.LastToken())
		}

		if p.IsEos() {
			t.Error("is eos")
		}

		if !p.ParseIdent() {
			t.Error("failed parse B")
		}

	} else {
		t.Error("failed parse")
	}
}

func TestParseTokenIncludeSpace(t *testing.T) {
	p := parser.NewParser(" a-b c   d")

	if p.ParseToken("a-b c ") {
		if p.LastToken() != "a-b c " {
			t.Errorf("unmatch: %s", p.LastToken())
		}

		if !p.IsNotEos() {
			t.Error("is eos")
		}
	} else {
		t.Error("failed parse")
	}
}

func TestParseTokenShortLength(t *testing.T) {
	p := parser.NewParser(" wit")

	if p.ParseToken("with") {
		t.Error("sucess parse")
	}
}

func TestParseTokenUnmatch(t *testing.T) {
	p := parser.NewParser(" wit A1 ")

	if p.ParseToken("with") {
		t.Error("sucess parse")
	}
}

func TestIsEos(t *testing.T) {
	p := parser.NewParser("  A=B  ")

	if p.IsEos() || !p.IsNotEos() {
		t.Error("is eos")
	}

	p.ParseIdent()
	p.ParseRune('=')
	p.ParseIdent()

	if p.IsEos() {
		t.Error("is eos")
	}

	if !p.ParseIdent() {
		if !p.IsEos() {
			t.Error("no skip space")
		}
	} else {
		t.Error("failed parse ident")
	}
}
