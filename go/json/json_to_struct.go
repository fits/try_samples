package main

import (
	"encoding/json"
	"fmt"
)

func main() {
	s := `
		{
			"item": {
				"id": "item-1",
				"value": 1
			}
		}
	`

	b := []byte(s)

	fmt.Println(s)
	fmt.Printf("%#v\n", b)

	println("-----")

	var it struct {
		Item struct {
			ID string
			Value int32
		}
	}

	err := json.Unmarshal(b, &it)

	if err != nil {
		panic(err)
	}

	fmt.Printf("%#v\n", it)
	fmt.Println("id =", it.Item.ID, ", value =", it.Item.Value)

	println("-----")

	var it2 struct {
		Item struct {
			ID string
			Category string
			Value int32
		}
	}

	err = json.Unmarshal(b, &it2)

	if err != nil {
		panic(err)
	}

	fmt.Printf("%#v\n", it2)
}
