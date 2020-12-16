package main

import (
	"fmt"
	"reflect"
)

type Item struct {
	id    string
	Name  string
	Value int32
}

func (i *Item) ID() string {
	return i.id
}

func (i *Item) show1() string {
	return fmt.Sprintf("id=%s, name=%s, value=%d", i.id, i.Name, i.Value)
}

func (i *Item) Show2() string {
	return i.show1()
}

func (i Item) plus1(t Item) Item {
	if i.id != t.id {
		return i
	}

	return Item{i.id, i.Name, i.Value + t.Value}
}

func (i Item) Plus2(t Item) Item {
	return i.plus1(t)
}

func printMethods(t reflect.Type) {
	for i := 0; i < t.NumMethod(); i++ {
		fmt.Println("method:", t.Method(i))
	}
}

func printFields(t reflect.Type) {
	for i := 0; i < t.NumField(); i++ {
		fmt.Println("field:", t.Field(i))
	}
}

func main() {
	println("--- *Item ---")
	t1 := reflect.TypeOf((*Item)(nil))
	printMethods(t1)
	// printFields(t1) // panic

	println("--- Item ---")
	printMethods(t1.Elem())
	printFields(t1.Elem())

	fmt.Println("t1.Kind():", t1.Kind())
	fmt.Println("t1.Elem().Kind():", t1.Elem().Kind())

	println("--- Item ---")
	t2 := reflect.TypeOf(Item{})
	printMethods(t2)
	printFields(t2)

	println("--- *Item ---")
	t3 := reflect.TypeOf(&Item{})
	printMethods(t3)
}
