package main

import (
	"fmt"
	"reflect"
)

type Data struct {
	Name string
}

func main() {
	// true
	fmt.Println(reflect.ValueOf((*Data)(nil)).IsZero())
	fmt.Println(reflect.ValueOf("").IsZero())
	fmt.Println(reflect.ValueOf(0).IsZero())
	fmt.Println(reflect.ValueOf(Data{}).IsZero())
	fmt.Println(reflect.ValueOf(Data{""}).IsZero())

	var d []Data
	fmt.Println(reflect.ValueOf(d).IsZero())

	println("-----")

	// false
	fmt.Println(reflect.ValueOf("a").IsZero())
	fmt.Println(reflect.ValueOf(1).IsZero())
	fmt.Println(reflect.ValueOf(Data{"a"}).IsZero())
	fmt.Println(reflect.ValueOf([]Data{}).IsZero())
	fmt.Println(reflect.ValueOf(&Data{}).IsZero())
}
