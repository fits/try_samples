package main

import (
	"fmt"
	i "sample/item"
)

func printItem(v i.Item) {
	fmt.Println("name =", v.Name(), ", value =", v.Value())
}

func main() {
	v := i.NewItem("item-1", 123)

	printItem(v)

	fmt.Printf("%#v\n", v)
}
