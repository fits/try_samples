package main

import (
	"fmt"
	"go.mongodb.org/mongo-driver/bson"
)

type Item struct {
	Name string
	Value int32
}

func main() {
	d := Item{"item-1", 123}

	doc, err := bson.Marshal(d)

	if err != nil {
		panic(err)
	}

	fmt.Println(doc)

	var d2 Item
	err = bson.Unmarshal(doc, &d2)

	if err != nil {
		panic(err)
	}

	fmt.Printf("%#v\n", d2)
}