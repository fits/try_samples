package main

import (
	"fmt"
)

type Id string
type Quantity uint

type Stock interface {
	isStock()
}

type EmptyStock struct {
	id Id
}

func (EmptyStock) isStock() {}

type NonEmptyStock struct {
	id  Id
	qty Quantity
}

func (NonEmptyStock) isStock() {}

func invoke[S Stock, T Stock](v S, f func(T)) bool {
	x, ok := any(v).(T)

	if !ok {
		return false
	}

	f(x)

	return true
}

type StockFunc struct {
	empty    func(EmptyStock)
	nonEmpty func(NonEmptyStock)
}

func call(s Stock, f StockFunc) {
	switch x := s.(type) {
	case EmptyStock:
		if f.empty != nil {
			f.empty(x)
		}
	case NonEmptyStock:
		if f.nonEmpty != nil {
			f.nonEmpty(x)
		}
	}
}

func main() {
	var s1 Stock = EmptyStock{"s1"}

	var r1 = invoke(s1, func(x EmptyStock) {
		fmt.Printf("empty id=%s \n", x.id)
	})

	fmt.Printf("r1 = %t \n", r1)

	var r2 = invoke(s1, func(x NonEmptyStock) {
		println("non empty")
	})

	fmt.Printf("r2 = %t \n", r2)

	call(s1, StockFunc{})

	call(s1, StockFunc{
		empty: func(x EmptyStock) {
			fmt.Printf("empty id=%s \n", x.id)
		},
	})

	var s2 Stock = NonEmptyStock{"s2", 3}

	call(s2, StockFunc{
		empty: func(x EmptyStock) {
			fmt.Printf("empty id=%s \n", x.id)
		},
	})

	call(s2, StockFunc{
		nonEmpty: func(x NonEmptyStock) {
			fmt.Printf("non empty id=%s, qty=%d \n", x.id, x.qty)
		},
	})
}
