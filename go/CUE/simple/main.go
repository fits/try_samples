package main

import (
	"fmt"
	"log"

	"cuelang.org/go/cue"
	"cuelang.org/go/cue/cuecontext"
)

func main() {
	ctx := cuecontext.New()

	v := ctx.CompileString(`
		import "math"

		x: 3
		y: 6 | *7 | 8
		
		calc: x * 2 + y / 2
		// calc_int: int & calc
		calc_int: int & math.Ceil(calc)
		calc_num: number & calc

		res: { first: calc, second: calc_int }

		#Data: {
			a: number
			b: int | *null
		}

		data1: #Data & { a: calc } & { b: calc_int }
		data2: #Data & { a: calc }
	`)

	if v.Err() != nil {
		log.Fatal(v.Err())
	}

	fmt.Printf("%#v\n", v)

	fmt.Println("-----")

	fmt.Printf("%v\n", v)

	fmt.Println("-----")

	fmt.Printf("x=%v\n", v.LookupPath(cue.ParsePath("x")))
	fmt.Printf("y=%v\n", v.LookupPath(cue.ParsePath("y")))

	fmt.Printf("calc=%v\n", v.LookupPath(cue.ParsePath("calc")))
	fmt.Printf("calc_int=%v\n", v.LookupPath(cue.ParsePath("calc_int")))
	fmt.Printf("res=%v\n", v.LookupPath(cue.ParsePath("res")))

	fmt.Println("-----")

	fmt.Printf("data1=%v\n", v.LookupPath(cue.ParsePath("data1")))
	fmt.Printf("data2=%v\n", v.LookupPath(cue.ParsePath("data2")))

	fmt.Println("-----")

	j, err := v.MarshalJSON()

	if err != nil {
		log.Fatal(err)
	}

	fmt.Println(string(j))
}
