package main

//go:generate echo generate1
//go:generate go run cmd/t1.go a bb ccc

func main() {
	println("main")
}
