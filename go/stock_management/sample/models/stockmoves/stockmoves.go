package stockmoves

import m "sample/models"
import s "sample/models/stocks"

type StockMove interface {
	applyEvent(event m.StockMoveEvent) StockMove
}

type StockMoveInfo struct {
	Item m.Item
	Qty m.Quantity
	From m.Location
	To m.Location
}

type NothingStockMove struct {}

type DraftStockMove struct {
	Info StockMoveInfo
}

type CompletedStockMove struct {
	Info StockMoveInfo
	Outgoing m.Quantity
	Incoming m.Quantity
}

type CancelledStockMove struct {
	Info StockMoveInfo
}

type AssignedStockMove struct {
	Info StockMoveInfo
	Assigned m.Quantity
}

type ShippedStockMove struct {
	Info StockMoveInfo
	Outgoing m.Quantity
}

type ArrivedStockMove struct {
	Info StockMoveInfo
	Outgoing m.Quantity
	Incoming m.Quantity
}

type AssignFailedStockMove struct {
	Info StockMoveInfo
}

type ShipmentFailedStockMove struct {
	Info StockMoveInfo
}

func (s NothingStockMove) applyEvent(event m.StockMoveEvent) StockMove {
	switch e := event.(type) {
	case m.Started:
		info := StockMoveInfo{Item: e.Item, Qty: e.Qty, From: e.From, To: e.To}
		return DraftStockMove{info}
	}

	return s
}

func (s DraftStockMove) applyEvent(event m.StockMoveEvent) StockMove {
	switch e := event.(type) {
	case m.Cancelled:
		return CancelledStockMove{s.Info}
	case m.Assigned:
		if e.Assigned > 0 {
			return AssignedStockMove{s.Info, e.Assigned}
		} else {
			return AssignFailedStockMove{s.Info}
		}
	case m.Shipped:
		if e.Outgoing > 0 {
			return ShippedStockMove{s.Info, e.Outgoing}
		} else {
			return ShipmentFailedStockMove{s.Info}
		}
	case m.Arrived:
		var outgoing m.Quantity = 0
		return ArrivedStockMove{s.Info, outgoing, e.Incoming}
	}

	return s
}

func (s CompletedStockMove) applyEvent(event m.StockMoveEvent) StockMove {
	return s
}

func (s CancelledStockMove) applyEvent(event m.StockMoveEvent) StockMove {
	return s
}

func (s AssignedStockMove) applyEvent(event m.StockMoveEvent) StockMove {
	switch e := event.(type) {
	case m.AssignShipped:
		if e.Outgoing > 0 {
			return ShippedStockMove{s.Info, e.Outgoing}

		} else {
			return ShipmentFailedStockMove{s.Info}
		}
	}

	return s
}

func (s ShippedStockMove) applyEvent(event m.StockMoveEvent) StockMove {
	switch e := event.(type) {
	case m.Arrived:
		return ArrivedStockMove{s.Info, s.Outgoing, e.Incoming}
	}

	return s
}

func (s ArrivedStockMove) applyEvent(event m.StockMoveEvent) StockMove {
	switch event.(type) {
	case m.Completed:
		return CompletedStockMove{s.Info, s.Outgoing, s.Incoming}
	}

	return s
}

func (s AssignFailedStockMove) applyEvent(event m.StockMoveEvent) StockMove {
	return s
}

func (s ShipmentFailedStockMove) applyEvent(event m.StockMoveEvent) StockMove {
	return s
}

func info(state StockMove) (StockMoveInfo, bool) {
	switch s := state.(type) {
	case DraftStockMove:
		return s.Info, true
	case CompletedStockMove:
		return s.Info, true
	case CancelledStockMove:
		return s.Info, true
	case AssignedStockMove:
		return s.Info, true
	case ShippedStockMove:
		return s.Info, true
	case ArrivedStockMove:
		return s.Info, true
	case AssignFailedStockMove:
		return s.Info, true
	case ShipmentFailedStockMove:
		return s.Info, true
	}

	return StockMoveInfo{}, false
}

func InitialState() StockMove {
	return NothingStockMove{}
}

func Start(state StockMove, item m.Item, qty m.Quantity, 
	from m.Location, to m.Location) (StockMove, m.StockMoveEvent) {

	event := m.Started{item, qty, from, to}
	return apply(state, event)
}

func Complete(state StockMove) (StockMove, m.StockMoveEvent) {
	event := m.Completed{}
	return apply(state, event)
}

func Cancel(state StockMove) (StockMove, m.StockMoveEvent) {
	event := m.Cancelled{}
	return apply(state, event)
}

func Assign(state StockMove, stock s.Stock) (StockMove, m.StockMoveEvent) {
	info, ok := info(state)

	if !ok {
		return state, emptyEvent()
	}

	var assigned m.Quantity = 0

	if stock.IsTarget(info.Item, info.From) && stock.IsSufficient(info.Qty) {

		assigned = info.Qty
	}

	event := m.Assigned{info.Item, info.From, assigned}
	return apply(state, event)
}

func Ship(state StockMove, outgoing m.Quantity) (StockMove, m.StockMoveEvent) {
	info, ok := info(state)

	if !ok {
		return state, emptyEvent()
	}

	s, ok := state.(AssignedStockMove)

	var event m.StockMoveEvent

	if ok {
		event = m.AssignShipped{info.Item, info.From, outgoing, s.Assigned}
	} else {
		event = m.Shipped{info.Item, info.From, outgoing}
	}

	return apply(state, event)
}

func Arrive(state StockMove, incoming m.Quantity) (StockMove, m.StockMoveEvent) {
	info, ok := info(state)

	if !ok {
		return state, emptyEvent()
	}

	event := m.Arrived{info.Item, info.To, incoming}
	return apply(state, event)
}

func apply(state StockMove, event m.StockMoveEvent) (StockMove, m.StockMoveEvent) {
	current := state
	state = state.applyEvent(event)

	if state == current {
		return state, emptyEvent()
	}

	return state, event
}

func emptyEvent() m.StockMoveEvent {
	return m.Nothing{}
}

func IsNothing(event m.StockMoveEvent) bool {
	return event == emptyEvent()
}

func Restore(state StockMove, events []m.StockMoveEvent) StockMove {
	s := state

	for _, ev := range events {
		s = s.applyEvent(ev)
	}

	return s
}
