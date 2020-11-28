package main

import (
	"encoding/json"
	"fmt"
	cloudevents "github.com/cloudevents/sdk-go/v2"
)

func main() {
	event := cloudevents.NewEvent()
	event.SetID("a1")
	event.SetSource("sample")
	event.SetType("sample.create")

	fmt.Println(event)

	buf, err := json.Marshal(event)

	if err != nil {
		panic(err)
	}

	println(string(buf))
}
