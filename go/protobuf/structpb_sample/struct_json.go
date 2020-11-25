package main

import (
	"fmt"
	"google.golang.org/protobuf/types/known/structpb"
)

func main() {
	s := `{
		"id": "item1",
		"value": 123,
		"details": {
			"code": "a1"
		}
	}`

	v1, err := structpb.NewStruct(nil)

	if err != nil {
		panic(err)
	}

	fmt.Println(s)
	fmt.Printf("%#v\n", v1)

	println("-----")

	err = v1.UnmarshalJSON([]byte(s))

	if err != nil {
		panic(err)
	}

	fmt.Printf("%#v\n", v1)
	fmt.Printf("%#v\n", v1.AsMap())

	b1, err := v1.MarshalJSON()

	if err != nil {
		panic(err)
	}

	fmt.Println(string(b1))

	println("-----")

	v2 := structpb.NewStructValue(v1)

	fmt.Printf("%#v\n", v2)

	b2, err := v2.MarshalJSON()

	if err != nil {
		panic(err)
	}

	fmt.Println(string(b2))
}
