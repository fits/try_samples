package main

import (
	"fmt"
	m "sample/models"
	v "sample/models/stockmoves"
	s "sample/models/stocks"
)

func printResult(state v.StockMove, event m.StockMoveEvent) {
	fmt.Println("state = ", state, ", event = ", event)
}

func sample1() {
	s0 := v.InitialState()
	fmt.Println(s0)

	s1, e1 := v.Start(s0, "item-1", 2, "loc-A", "loc-B")

	printResult(s1, e1)

	if v.IsNothing(e1) {
		panic("start error")
	}

	s2, e2 := v.Ship(s1, 2)

	printResult(s2, e2)

	if v.IsNothing(e2) {
		panic("ship error")
	}

	s3, e3 := v.Arrive(s2, 2)

	printResult(s3, e3)

	if v.IsNothing(e3) {
		panic("arrive error")
	}

	s4, e4 := v.Complete(s3)

	printResult(s4, e4)

	if v.IsNothing(e4) {
		panic("complete error")
	}

	_, e3b := v.Cancel(s2)

	if v.IsNothing(e3b) {
		println("failed cancel")
	}

	rs := v.Restore(s0, []m.StockMoveEvent{e1, e2})
	fmt.Println(rs)
}

func sample2() {
	s0 := v.InitialState()
	fmt.Println(s0)

	s1, e1 := v.Start(s0, "item-2", 2, "loc-A", "loc-B")

	printResult(s1, e1)

	if v.IsNothing(e1) {
		panic("start error")
	}

	stock := s.InitialUnmanaged("item-2", "loc-A")

	s2, e2 := v.Assign(s1, stock)

	printResult(s2, e2)

	if v.IsNothing(e2) {
		panic("assign error")
	}

	s3, e3 := v.Ship(s2, 2)

	printResult(s3, e3)

	if v.IsNothing(e3) {
		panic("ship error")
	}
}

func sample3() {
	s0 := v.InitialState()
	fmt.Println(s0)

	s1, e1 := v.Start(s0, "item-3", 2, "loc-A", "loc-B")

	printResult(s1, e1)

	if v.IsNothing(e1) {
		panic("start error")
	}

	stock := s.InitialManaged("item-3", "loc-A")

	s2, e2 := v.Assign(s1, stock)

	printResult(s2, e2)

	if v.IsNothing(e2) {
		panic("assign error")
	}

	_, ok := s2.(v.AssignFailedStockMove)

	if ok {
		println("assign failed")
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
