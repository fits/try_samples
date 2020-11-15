package main

import "fmt"

type StockId string
type Quantity uint32

type StockEvent interface {
	stockEvent()
}

type CreatedStock struct {
	id StockId
}

type UpdatedStock struct {
	id StockId
	qty Quantity
}

func (_ CreatedStock) stockEvent() {}
func (_ UpdatedStock) stockEvent() {}

type Stock interface {
	applyEvent(event StockEvent) Stock
}

type Nothing struct {}

type OutOfStock struct {
	id StockId
}

type InStock struct {
	id StockId
	qty Quantity
}

func (s Nothing) applyEvent(event StockEvent) Stock {
	switch e := event.(type) {
	case CreatedStock:
		return OutOfStock{id: e.id}
	}

	return s
}

func (s OutOfStock) applyEvent(event StockEvent) Stock {
	switch e := event.(type) {
	case UpdatedStock:
		if e.id == s.id && e.qty > 0 {
			return InStock{id: s.id, qty: e.qty}
		}
	}

	return s
}

func (s InStock) applyEvent(event StockEvent) Stock {
	switch e := event.(type) {
	case UpdatedStock:
		if e.id == s.id {
			if e.qty == 0 {
				return OutOfStock{id: s.id}
			} else if e.qty != s.qty {
				return InStock{id: s.id, qty: e.qty}
			}
		}
	}

	return s
}

func main() {
	s := Nothing{}
	fmt.Println(s)

	ev1 := CreatedStock{id: "stock-1"}
	s11 := s.applyEvent(ev1)
	fmt.Println(s11)

	ev2 := UpdatedStock{id: "stock-1", qty: 5}
	s12 := s11.applyEvent(ev2)
	fmt.Println(s12)

	ev3 := UpdatedStock{id: "stock-1", qty: 2}
	s13 := s12.applyEvent(ev3)
	fmt.Println(s13)

	ev4 := UpdatedStock{id: "stock-1", qty: 0}
	s14 := s13.applyEvent(ev4)
	fmt.Println(s14)

	s21 := s.applyEvent(UpdatedStock{id: "stock-2", qty: 10})
	fmt.Println(s21)
}
