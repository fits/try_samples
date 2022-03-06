package main

import "fmt"

func main() {
	ps := []string{"a1", "b2", "c3", "d4", "e5"}

	fmt.Printf("%v\n", ps[1:])
	fmt.Printf("%v\n", ps[2:4])

	s := 3
	e := 5
	
	fmt.Printf("%v\n", ps[s:e])
	fmt.Printf("%v\n", ps[s:s])
}
