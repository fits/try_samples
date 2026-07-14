package main

import (
	"fmt"
	"go/ast"
	"go/parser"
	"go/token"
	"log"
	"os"
)

func main() {
	file := os.Args[1]

	f, err := parser.ParseFile(token.NewFileSet(), file, nil, 0)

	if err != nil {
		log.Fatal(err)
	}

	fmt.Printf("package=%v, %s \n", f.Name, f.Name.Name)

	for _, d := range f.Decls {
		switch t := d.(type) {
		case *ast.GenDecl:
			fmt.Printf("GenDecl tok=%v \n", t.Tok)

			for _, s := range t.Specs {
				switch u := s.(type) {
				case *ast.ImportSpec:
					fmt.Printf("* ImportSpec name=%v, path=%s \n", u.Name, u.Path.Value)
				case *ast.ValueSpec:
					fmt.Printf("* ValueSpec names=%v \n", u.Names)
				case *ast.TypeSpec:
					fmt.Printf("* TypeSpec name=%v \n", u.Name)
				}
			}
		case *ast.FuncDecl:
			fmt.Printf("FuncDecl name=%v, type=%v \n", t.Name, t.Type)
		case *ast.BadDecl:
			fmt.Printf("BadDecl from=%v, to=%v \n", t.From, t.To)
		}
	}
}
