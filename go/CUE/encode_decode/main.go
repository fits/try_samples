package main

import (
	"fmt"
	"log"

	"cuelang.org/go/cue"
	"cuelang.org/go/cue/cuecontext"
)

func main() {
	ctx := cuecontext.New()

	schema := ctx.CompileString(`
		import "strings"

		#Data: {
			name: string
			_name_trim: strings.TrimSpace(name)
			_name_len: len(_name_trim) & (> 0)

			type: *"A1" | "B2" | "C3"
		}
	`)

	if schema.Err() != nil {
		log.Fatal(schema.Err())
	}

	type Data struct {
		Name string `json:"name"`
		Type string `json:"type"`
	}

	type Data2 struct {
		Name string  `json:"name"`
		Type *string `json:"type"`
	}

	dataType := schema.LookupPath(cue.ParsePath("#Data"))

	fmt.Printf("test1=%v\n", dataType.Unify(ctx.CompileString(`{name: "aaaa"}`)))
	fmt.Printf("test2=%v\n", dataType.Unify(ctx.CompileString(`{name: "   "}`)))
	fmt.Printf("test3=%v\n", dataType.Unify(ctx.CompileString(`{name: "  a ", value: 12}`)))
	fmt.Printf("test4=%v\n", dataType.Unify(ctx.CompileString(`{name: " abc ", type: "B2"}`)))
	fmt.Printf("test5=%v\n", dataType.Unify(ctx.CompileString(`{name: " abc ", type: "B5"}`)))

	v := dataType.Unify(ctx.CompileString(`{name: "abc123"}`))

	s, err := v.MarshalJSON()

	if err != nil {
		log.Fatal(err)
	}

	fmt.Println(string(s))

	var d Data
	err = v.Decode(&d)

	if err != nil {
		log.Fatal(err)
	}

	fmt.Printf("decode to struct=%v\n", d)

	fmt.Println("-----")

	d1 := Data{"test-1", "B2"}
	v1 := ctx.Encode(d1)

	fmt.Printf("encode from struct=%v\n", v1)
	fmt.Printf("type unify=%v\n", dataType.Unify(v1))

	fmt.Println("-----")

	d2 := Data{"   ", "B2"}
	v2 := ctx.Encode(d2)

	fmt.Printf("encode from struct=%v\n", v2)
	fmt.Printf("type unify=%v\n", dataType.Unify(v2))

	fmt.Println("-----")

	d3 := Data{"test-3", "D5"}
	v3 := ctx.Encode(d3)

	fmt.Printf("encode from struct=%v\n", v3)
	fmt.Printf("type unify=%v\n", dataType.Unify(v3))

	fmt.Println("-----")

	d4 := Data{"test-4", ""}
	v4 := ctx.Encode(d4)

	fmt.Printf("encode from struct=%v\n", v4)
	fmt.Printf("type unify=%v\n", dataType.Unify(v4))

	fmt.Println("-----")

	d5 := Data2{"test-5", nil}
	v5 := ctx.Encode(d5)

	fmt.Printf("encode from struct=%v\n", v5)
	fmt.Printf("type unify=%v\n", dataType.Unify(v5))

	fmt.Println("-----")
}
