package main

import (
	"fmt"
	"reflect"
)

func main() {
	var f = func(a string, b bool) int {
		if b {
			return len(a)
		} else {
			return 0
		}
	}

	fmt.Printf("called f1: %d \n", f("abc", true))

	var t = reflect.TypeOf(f)

	fmt.Printf("%v \n", t)

	fmt.Printf("name=%s, numIn=%d, arg0=%v, arg1=%v, return=%v \n", t.Name(), t.NumIn(), t.In(0), t.In(1), t.Out(0))
}
