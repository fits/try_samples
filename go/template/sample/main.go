package main

import (
	"strings"
	"text/template"
)

type Item struct {
	Name     string
	Editions []Edition
}

type Edition struct {
	Name  string
	Price int
}

func main() {
	tmpl, err := template.New("sample").Parse(`
[Item]
  name: {{.Name}}
[Editions]
{{- range .Editions}}
  name: {{.Name}}, price: {{.Price}}
  {{- if ge .Price 2000}} (>= 2000) {{end}}
{{- end}}
	`)

	if err != nil {
		panic(err)
	}

	d := Item{
		"item1",
		[]Edition{
			{"Standard", 1000},
			{"Extra", 2000},
			{"Premium", 3000},
		},
	}

	writer := new(strings.Builder)

	err = tmpl.Execute(writer, d)

	if err != nil {
		panic(err)
	}

	r := strings.TrimSpace(writer.String())

	println(r)
}
