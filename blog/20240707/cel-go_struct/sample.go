package main

import (
	"fmt"
	"log"
	"os"

	"github.com/google/cel-go/cel"
)

func main() {
	name := os.Args[1]

	env, err := cel.NewEnv(cel.Variable("name", cel.StringType))

	if err != nil {
		log.Fatal(err)
	}

	ast, iss := env.Compile("size(name) > 3")

	if iss.Err() != nil {
		log.Fatal(iss.Err())
	}

	prg, err := env.Program(ast)

	if err != nil {
		log.Fatal(err)
	}

	out, _, err := prg.Eval(map[string]any{
		"name": name,
	})

	if err != nil {
		log.Fatal(err)
	}

	fmt.Println(out)
	fmt.Printf("type=%v, value=%v \n", out.Type(), out.Value())
}
