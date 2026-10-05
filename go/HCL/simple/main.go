package main

import (
	"fmt"
	"log"

	"github.com/hashicorp/hcl/v2/hclsimple"
)

type Config struct {
	X int `hcl:"x"`
	Y int `hcl:"y"`

	Variant []Variant `hcl:"variant,block"`
}

type Variant struct {
	Name  string  `hcl:"name,label"`
	Size  *string `hcl:"size"`
	Color string  `hcl:"color,optional"`
}

func main() {
	test1()

	println("-----")

	test2()
}

func test1() {
	s := `
		x = 3
		y = 5

		calc = x * 2 + y / 2
		calc_int = ceil(calc)
	`

	var data map[string]any

	err := hclsimple.Decode("config.hcl", []byte(s), nil, &data)

	if err != nil {
		log.Fatal(err)
	}

	for k, v := range data {
		fmt.Printf("* key=%s, value=%#v \n", k, v)
	}
}

func test2() {
	s := `
		x = 1
		y = 2

		variant "L-WHT" {
			size = "L"
			color = "white"
		}

		variant "S-BLK" {
			size = "S"
			color = "black"
		}

		variant "GRN" {
			color = "green"
		}

		variant "Free" {
			size = "F"
		}
	`

	var config Config

	err := hclsimple.Decode("config.hcl", []byte(s), nil, &config)

	if err != nil {
		log.Fatal(err)
	}

	fmt.Printf("%#v \n", config)
}
