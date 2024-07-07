package main

import (
	"fmt"
	"log"
	"reflect"

	"github.com/google/cel-go/cel"
	"github.com/google/cel-go/ext"
)

type Data struct {
	ID    string
	Value int
}

func main() {
	env, err := cel.NewEnv(
		cel.Variable("data", cel.ObjectType("main.Data")),
		ext.NativeTypes(reflect.TypeFor[Data]()),
	)

	if err != nil {
		log.Fatal(err)
	}

	ast, iss := env.Compile(`"id=" + data.ID + ", value=" + string(data.Value)`)

	if iss.Err() != nil {
		log.Fatal(iss.Err())
	}

	prg, err := env.Program(ast)

	if err != nil {
		log.Fatal(err)
	}

	d := Data{"d-1", 1000}

	out, _, err := prg.Eval(map[string]any{
		"data": d,
	})

	if err != nil {
		log.Fatal(err)
	}

	fmt.Println(out)
}
