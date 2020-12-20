package main

import (
	"fmt"
	"reflect"
)

type Any interface {} 

type Data struct {
	Name string
}

func isNilAny(d Any) bool {
	switch v := d.(type) {
	case *Data:
		return v == nil
	}

	return false
}

func isNil(i interface{}) bool {
	if i == nil {
		return true
	}

	v := reflect.ValueOf(i)

	switch v.Kind() {
	case reflect.Chan, reflect.Func, reflect.Interface, 
	reflect.Map, reflect.Ptr, reflect.Slice:
		return v.IsNil()
	}

	return false
}

func main() {
	a := (*Data)(nil)

	fmt.Printf("(a) %#v == nil : %v\n", a, a == nil) // true

	var b Any
	b = (*Data)(nil)

	fmt.Printf("(b1) == nil : %v\n", b == nil) // false
	fmt.Printf("(b2) == nil : %v\n", b == (*Data)(nil)) // true
	fmt.Printf("(b3) == nil : %v\n", isNilAny(b)) // true

	var c interface{}
	c = (*Data)(nil)

	fmt.Printf("(c1) == nil : %v\n", c == nil) // false
	fmt.Printf("(c2) == nil : %v\n", c == (*Data)(nil)) // true
	fmt.Printf("(c3) == nil : %v\n", isNilAny(c)) // true


	fmt.Printf("isNilAny(interface{}) == nil : %v\n", isNilAny((interface{})(nil))) // false

	fmt.Printf("isNil(a) == nil : %v\n", isNil(a)) // true
	fmt.Printf("isNil(\"\") == nil : %v\n", isNil("")) // false
	fmt.Printf("isNil(0) == nil : %v\n", isNil(0)) // false
}
