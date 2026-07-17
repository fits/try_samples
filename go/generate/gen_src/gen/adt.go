package main

import (
	"fmt"
	"os"
	"regexp"
	"strings"
)

func main() {
	fmt.Printf("file=%s, line=%s, package=%s \n", os.Getenv("GOFILE"), os.Getenv("GOLINE"), os.Getenv("GOPACKAGE"))

	targetPackage := os.Getenv("GOPACKAGE")
	targetFile := os.Getenv("GOFILE")

	arg := os.Args[1]
	st := strings.Split(arg, "=")

	if len(st) != 2 {
		fmt.Fprintf(os.Stderr, "syntax error: %s \n", arg)
		os.Exit(1)
	}

	ls := strings.TrimSpace(st[0])
	rs := strings.TrimSpace(st[1])

	if !isIdent(ls) {
		fmt.Fprintf(os.Stderr, "syntax error: not ident (%s) \n", ls)
		os.Exit(1)
	}

	var els []string

	for _, s := range strings.Split(rs, "|") {
		s = strings.TrimSpace(s)

		if isIdent(s) {
			els = append(els, s)
		}
	}

	if len(els) < 2 {
		fmt.Fprintf(os.Stderr, "syntax error: at least two elements are required (%v) \n", els)
		os.Exit(1)
	}

	fileName := fmt.Sprintf("gen_%s_%s", strings.ToLower(ls), targetFile)

	methodName := fmt.Sprintf("is%s", ls)

	body := fmt.Sprintf("package %s \n", targetPackage)
	body += fmt.Sprintf("type %s interface { %s() } \n", ls, methodName)

	for _, el := range els {
		body += fmt.Sprintf("func (%s) %s() {} \n", el, methodName)
	}

	err := os.WriteFile(fileName, []byte(body), 0644)

	if err != nil {
		fmt.Fprintf(os.Stderr, "failed generate file: %v \n", err)
		os.Exit(1)
	}
}

func isIdent(s string) bool {
	r, _ := regexp.MatchString(`[0-9a-zA-Z_]+`, s)
	return r
}
