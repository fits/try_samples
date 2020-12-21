package main

import (
	"fmt"
	m "sample/models"
	v "sample/models/stockmoves"
	s "sample/models/stocks"
)

func printResult(res *v.StockMoveResult) {
	fmt.Printf("state = %#v, event = %#v\n", res.State, res.Event)
}

func sample1() {
	s0 := v.InitialState()
	fmt.Printf("initial state = %#v\n", s0)

	r1 := v.Start(s0, "item-1", 2, "loc-A", "loc-B")

	if r1 == nil {
		panic("start error")
	}

	printResult(r1)

	r2 := v.Ship(r1.State, 2)

	if r2 == nil {
		panic("ship error")
	}

	printResult(r2)

	r3 := v.Arrive(r2.State, 2)

	if r3 == nil {
		panic("arrive error")
	}

	printResult(r3)

	r4 := v.Complete(r3.State)

	if r4 == nil {
		panic("complete error")
	}

	printResult(r4)

	r3b := v.Cancel(r2.State)

	if r3b == nil {
		println("*** failed cancel")
	}

	rs := v.Restore(s0, []m.StockMoveEvent{r1.Event, r2.Event})
	fmt.Printf("%#v\n", rs)
}

func sample2() {
	s0 := v.InitialState()

	r1 := v.Start(s0, "item-2", 2, "loc-A", "loc-B")

	if r1 == nil {
		panic("start error")
	}

	printResult(r1)

	stock := s.InitialUnmanaged("item-2", "loc-A")

	r2 := v.Assign(r1.State, stock)

	if r2 == nil {
		panic("assign error")
	}

	printResult(r2)

	r3 := v.Ship(r2.State, 2)

	if r3 == nil {
		panic("ship error")
	}

	printResult(r3)
}

func sample3() {
	s0 := v.InitialState()

	r1 := v.Start(s0, "item-3", 2, "loc-A", "loc-B")

	if r1 == nil {
		panic("start error")
	}

	printResult(r1)

	stock := s.InitialManaged("item-3", "loc-A")

	r2 := v.Assign(r1.State, stock)

	if r2 == nil {
		panic("assign error")
	}

	printResult(r2)

	_, ok := r2.State.(v.AssignFailedStockMove)

	if ok {
		println("*** assign failed")
	} else {
		panic("invalid state")
	}
}

func main() {
	sample1()

	println("-----")

	sample2()

	println("-----")

	sample3()
}
