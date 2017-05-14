package main

import (
	"fmt"
)

func headValue(dic map[string]string) string {
	for _, v := range dic {
		return v
	}
	return ""
}

func main() {

	d := map[string]string{"a": "a1", "b": "b1"}

	fmt.Println(d)

	for k, v := range d {
		fmt.Printf("key=%s, value=%s\n", k, v)
	}

	fmt.Println(headValue(d))
	fmt.Println(headValue(map[string]string{}))
}
