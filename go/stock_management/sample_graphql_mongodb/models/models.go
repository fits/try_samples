package models

type Item = string
type Location = string
type Quantity = uint32

type StockMoveEvent interface {
	stockMoveEvent()
}

type Started struct {
	Item Item
	Qty Quantity
	From Location
	To Location
}

type Completed struct {}
type Cancelled struct {}

type Assigned struct {
	Item Item
	From Location
	Assigned Quantity
}

type Shipped struct {
	Item Item
	From Location
	Outgoing Quantity
}

type AssignShipped struct {
	Item Item
	From Location
	Outgoing Quantity
	Assigned Quantity
}

type Arrived struct {
	Item Item
	To Location
	Incoming Quantity
}

func (_ *Started) stockMoveEvent() {}
func (_ *Completed) stockMoveEvent() {}
func (_ *Cancelled) stockMoveEvent() {}
func (_ *Assigned) stockMoveEvent() {}
func (_ *Shipped) stockMoveEvent() {}
func (_ *AssignShipped) stockMoveEvent() {}
func (_ *Arrived) stockMoveEvent() {}
