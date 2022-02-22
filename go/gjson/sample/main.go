package main

import (
	"fmt"

	"github.com/tidwall/gjson"
)

func main() {
	d := `
		{
			"name": "item-1",
			"colors": ["white", "black"],
			"price": 1000,
			"in-stock": true,
			"details": {
				"date": "2022-02-23T02:03:00+09:00"
			}
		}
	`

	for _, f := range []string{"name", "colors", "price", "in-stock", "details"} {
		fmt.Printf("--- field: %s ---\n", f)

		v := gjson.Get(d, f).Value()

		switch t := v.(type) {
		case string:
			fmt.Printf("string = %s\n", t)
		case []interface{}:
			fmt.Printf("array = %v\n", t)
		case float64:
			fmt.Printf("float64 = %f\n", t)
		case bool:
			fmt.Printf("bool = %v\n", t)
		case map[string]interface{}:
			fmt.Printf("map = %v\n", t)
		default:
			fmt.Printf("unknown = %v\n", t)
		}
	}
}
