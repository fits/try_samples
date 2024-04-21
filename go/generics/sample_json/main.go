package main

import (
	"encoding/json"
	"fmt"
	"log"
)

type Json[V any] struct{}

func (*Json[V]) ToStr(v V) (string, error) {
	buf, err := json.Marshal(v)

	if err != nil {
		return "", err
	}

	return string(buf), nil
}

func (*Json[V]) Parse(s string) (*V, error) {
	var res V

	err := json.Unmarshal([]byte(s), &res)

	if err != nil {
		return nil, err
	}

	return &res, nil
}

type Item struct {
	ID    string `json:"id"`
	Value int    `json:"value"`
}

func main() {
	j := Json[Item]{}

	i1 := Item{"item-1", 123}

	s, err := j.ToStr(i1)

	if err != nil {
		log.Fatal(err)
	}

	fmt.Println(s)

	i2, err := j.Parse(s)

	if err != nil {
		log.Fatal(err)
	}

	fmt.Printf("%#v \n", i2)
}
