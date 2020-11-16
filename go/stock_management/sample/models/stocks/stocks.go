package stocks

import m "sample/models"

type Stock interface {
	IsTarget(item m.Item, location m.Location) bool
	IsSufficient(qty m.Quantity) bool
	applyEvent(event m.StockMoveEvent) Stock
}

type UnmanagedStock struct {
	Item m.Item
	Location m.Location
}

type ManagedStock struct {
	Item m.Item
	Location m.Location
	Qty m.Quantity
	Assigned m.Quantity
}

func (s UnmanagedStock) IsTarget(item m.Item, location m.Location) bool {
	return s.Item == item && s.Location == location
}

func (_ UnmanagedStock) IsSufficient(qty m.Quantity) bool {
	return true
}

func (s UnmanagedStock) applyEvent(event m.StockMoveEvent) Stock {
	return s
}

func (s ManagedStock) IsTarget(item m.Item, location m.Location) bool {
	return s.Item == item && s.Location == location
}

func (s ManagedStock) IsSufficient(qty m.Quantity) bool {
	return s.Qty >= s.Assigned + qty
}

func (s ManagedStock) applyEvent(event m.StockMoveEvent) Stock {
	return s
}

func InitialUnmanaged(item m.Item, location m.Location) Stock {
	return UnmanagedStock{item, location}
}

func InitialManaged(item m.Item, location m.Location) Stock {
	return ManagedStock{item, location, 0, 0}
}

func RestoreStock(state Stock, events []m.StockMoveEvent) Stock {
	s := state

	for _, ev := range events {
		s = s.applyEvent(ev)
	}

	return s
}
