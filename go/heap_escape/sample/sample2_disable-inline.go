// go 1.15
// go build -gcflags="-m -l" sample2_disable-inline.go

package main

type A struct {
	v string
}

type B struct {
	v interface{}
}

func newA(v string) A {
	return A{v}
}

func newRefA(v string) *A {
	return &A{v}
}

func newB(v interface{}) B {
	return B{v}
}

func newRefB(v interface{}) *B {
	return &B{v}
}

func newRefBstr(v string) *B {
	return &B{v} // v escapes to heap (-gcflags="-m -l")
}

func newRefBint(v int) *B {
	return &B{v} // v escapes to heap (-gcflags="-m -l")
}

func main() {
	newA("a-1")
	newRefA("a-2")

	newB("b-1")
	newRefB("b-2") // "b-2" escapes to heap (-gcflags="-m -l")
	newRefBstr("b-3")

	newB(11)
	newRefB(22) // 22 escapes to heap (-gcflags="-m -l")
	newRefBint(33)
}