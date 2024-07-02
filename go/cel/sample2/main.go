package main

import (
	"fmt"
	"log"
	"reflect"

	"github.com/google/cel-go/cel"
	"github.com/google/cel-go/ext"
)

type Item struct {
	ID    string
	Value int
}

func main() {
	env, err := cel.NewEnv(
		cel.Variable("item", cel.ObjectType("main.Item")),
		ext.NativeTypes(reflect.TypeFor[Item]()),
	)

	if err != nil {
		log.Fatal(err)
	}

	ast, iss := env.Compile(`"id=" + item.ID + ", value=" + string(item.Value)`)

	if iss.Err() != nil {
		log.Fatal(iss.Err())
	}

	prg, err := env.Program(ast)

	if err != nil {
		log.Fatal(err)
	}

	item := Item{"item-1", 1000}

	out, _, err := prg.Eval(map[string]any{
		"item": item,
	})

	if err != nil {
		log.Fatal(err)
	}

	fmt.Println(out)
}
