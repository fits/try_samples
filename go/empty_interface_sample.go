package main

import "fmt"

func Sample(v interface{}) {
	fmt.Println(v)

	switch v.(type) {
	case int:
		fmt.Println("int ", v)
	case string:
		fmt.Println("string ", v)
	default:
		fmt.Println("other ", v)
	}
}

func main() {
	Sample(123)
	Sample("abc")
	Sample(true)
}
