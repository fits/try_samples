package main

import "fmt"

//go:generate go run gen/adt.go "Data = Elem1 | Elem2"

type Elem1 struct {
	v int
}
type Elem2 struct {
	name string
}

func main() {
	var d1 Data = Elem1{123}
	d1.isData()

	var d2 = Elem2{"abc"}
	d2.isData()

	printData(d1)
	printData(d2)
}

func printData(d Data) {
	switch x := d.(type) {
	case Elem1:
		fmt.Printf("elem1: %d, %v \n", x.v, x)
	case Elem2:
		fmt.Printf("elem2: %s, %v \n", x.name, x)
	}
}
