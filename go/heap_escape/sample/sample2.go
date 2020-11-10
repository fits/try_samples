// go 1.15
// go build -gcflags="-m" sample2.go

package main

type A struct {
	v string
}

type B struct {
	v interface{}
}

func newA(v interface{}) A {
	switch v.(type) {
	case string:
		return A{v.(string)}
	default:
		panic("")
	}
}

func newRefA(v interface{}) *A {
	switch v.(type) {
	case string:
		return &A{v.(string)}
	default:
		panic("")
	}
}

func newB(v interface{}) B {
	switch v.(type) {
	case string:
		return B{v}
	case int:
		return B{v}
	default:
		panic("")
	}
}

func newRefB(v interface{}) *B {
	switch v.(type) {
	case string:
		return &B{v}
	case int:
		return &B{v}
	default:
		panic("")
	}
}

func main() {
	newA("a-1")
	newRefA("a-2")

	newB("b-1")
	newRefB("b-2") // "b-2" escapes to heap

	newB(11)
	newRefB(22) // 22 escapes to heap
}