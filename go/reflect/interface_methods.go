package main

import (
	"fmt"
	"reflect"
)

type Counter interface {
	Up(v int)
	Down(v int)
	Current() int
	reset()
	end() int
}

func printMethods(t reflect.Type) {
	for i := 0; i < t.NumMethod(); i++ {
		fmt.Println("method:", t.Method(i))
	}
}

func main() {
	t := reflect.TypeOf((*Counter)(nil)).Elem()
	printMethods(t)
}
