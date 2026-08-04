package main

import "fmt"

//go:generate go run gen/adt.go "Stock = OutOfStock | InStock"

type Id string
type Quantity uint

type OutOfStock struct {
	id Id
}

type InStock struct {
	id  Id
	qty Quantity
}

type StockFunc interface {
	Qty() Quantity
}

func (_ *OutOfStock) Qty() Quantity {
	return 0
}

func (s *InStock) Qty() Quantity {
	return s.qty
}

func main() {
	s1 := OutOfStock{"s-1"}

	fmt.Printf("s1: %+v \n", s1)
	fmt.Printf("s1 qty: %d \n", s1.Qty())

	s2 := InStock{"s-2", 5}

	fmt.Printf("s2: %+v \n", s2)
	fmt.Printf("s2 qty: %d \n", s2.Qty())
}
