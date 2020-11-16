package main

import (
	"errors"
	"fmt"
)

type ItemCode string
type Value int32

type Item struct {
	code ItemCode
	value Value
}

func sample11(code ItemCode, value Value) Item {
	if value % 2 == 0 {
		return Item{}
	}

	return Item{code, value}
}

func sample12(code ItemCode, value Value) (Item, error) {
	if value % 2 == 0 {
		return Item{}, errors.New("value must be odd")
	}

	return Item{code, value}, nil
}

func sample13(code ItemCode, value Value) (Item, bool) {
	if value % 2 == 0 {
		return Item{}, false
	}

	return Item{code, value}, true
}

func sample21(code ItemCode, value Value) *Item {
	if value % 2 == 0 {
		return nil
	}

	return &Item{code, value}
}

func sample22(code ItemCode, value Value) (*Item, error) {
	if value % 2 == 0{
		return nil, errors.New("value must be odd")
	}

	return &Item{code, value}, nil
}

func main() {
	fmt.Println(sample11("item11-1", 1))
	fmt.Println(sample11("item11-2", 2))

	fmt.Println(sample12("item12-1", 1))
	fmt.Println(sample12("item12-2", 2))

	fmt.Println(sample13("item13-1", 1))
	fmt.Println(sample13("item13-2", 2))

	fmt.Println(sample21("item21-1", 1))
	fmt.Println(sample21("item21-2", 2))

	fmt.Println(sample22("item22-1", 1))
	fmt.Println(sample22("item22-2", 2))
}
