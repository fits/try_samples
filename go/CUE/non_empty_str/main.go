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
		a: {x:1}
		a: {y:1}

		#Data: {
			name: string & (=~ "^.+")
			value: int & (> 0)
		
			note: string
			_note_len: len(note) & >0
		}
		
		r: #Data & { name: "abc", value: 3, note: "a"}
	`)

	if v.Err() != nil {
		log.Fatal(v.Err())
	}

	fmt.Printf("%v\n", v)

	fmt.Printf("r=%v\n", v.LookupPath(cue.ParsePath("r")))

	s, err := v.MarshalJSON()

	if err != nil {
		log.Fatal(err)
	}

	fmt.Println(string(s))

	fmt.Println("-----")

	t := v.LookupPath(cue.ParsePath("#Data"))

	fmt.Printf("test1=%v\n", t.Unify(ctx.CompileString(`{name: "aaaa", value: 1, note: "bbb"}`)))
	fmt.Printf("test2=%v\n", t.Unify(ctx.CompileString(`{name: "aaaa", value: 0, note: "bbb"}`)))
	fmt.Printf("test3=%v\n", t.Unify(ctx.CompileString(`{name: "", value: 1, note: "bbb"}`)))
	fmt.Printf("test4=%v\n", t.Unify(ctx.CompileString(`{name: "aaaa", value: 1, note: ""}`)))
}
