package main

import (
	"fmt"
	"unicode"
)

func isAlphaNumeric(r rune) bool {
	return ('a' <= r && r <= 'z') || ('A' <= r && r <= 'Z') || ('0' <= r && r <= '9')
}

func isAlphaNumeric2(r rune) bool {
	if r <= 'z' {
		if r >= 'a' {
			return true
		} else if r <= 'Z' {
			if r >= 'A' {
				return true
			} else {
				return '0' <= r && r <= '9'
			}
		}
	}
	return false
}

func checkAlphaNumeric(r rune) {
	fmt.Printf("'%c' is alphanumeric: %t \n", r, isAlphaNumeric(r))
	fmt.Printf("'%c' is alphanumeric2: %t \n", r, isAlphaNumeric2(r))
}

func checkLetter(r rune) {
	fmt.Printf("'%c' is letter: %t \n", r, unicode.IsLetter(r))
}

func checkNumber(r rune) {
	fmt.Printf("'%c' is number: %t \n", r, unicode.IsNumber(r))
}

func checkAsciiNumber(r rune) {
	res := r <= unicode.MaxASCII && unicode.IsNumber(r)
	fmt.Printf("'%c' is number: %t \n", r, res)
}

func main() {
	checkAlphaNumeric('0')
	checkAlphaNumeric('5')
	checkAlphaNumeric('9')
	checkAlphaNumeric('a')
	checkAlphaNumeric('Z')
	checkAlphaNumeric('_')
	checkAlphaNumeric(' ')
	checkAlphaNumeric('$')
	checkAlphaNumeric('あ')
	checkAlphaNumeric('ⅵ')

	fmt.Println("-----")

	checkLetter('0')
	checkLetter('Z')
	checkLetter('あ')
	checkLetter('_')
	checkLetter(' ')

	fmt.Println("-----")

	checkNumber('0')
	checkNumber('ⅵ')
	checkNumber('a')

	fmt.Println("-----")

	checkAsciiNumber('0')
	checkAsciiNumber('ⅵ')
	checkAsciiNumber('a')
}
