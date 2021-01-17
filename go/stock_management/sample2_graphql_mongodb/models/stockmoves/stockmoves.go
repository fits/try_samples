package stockmoves

import (
	m "sample/models"
	s "sample/models/stocks"
)

type StockMoveAction interface {
	stockMoveAction()
}

type StockMove interface {
	applyEvent(event m.StockMoveEvent) StockMove
}

type Start struct {
	Item m.Item
	Qty  m.Quantity
	From m.Location
	To   m.Location
}

type Complete struct{}
type Cancel struct{}

type Assign struct {
	Stock s.Stock
}

type Ship struct {
	Outgoing m.Quantity
}

type Arrive struct {
	Incoming m.Quantity
}

func (_ *Start) stockMoveAction()    {}
func (_ *Complete) stockMoveAction() {}
func (_ *Cancel) stockMoveAction()   {}
func (_ *Assign) stockMoveAction()   {}
func (_ *Ship) stockMoveAction()     {}
func (_ *Arrive) stockMoveAction()   {}

type StockMoveInfo struct {
	Item m.Item
	Qty  m.Quantity
	From m.Location
	To   m.Location
}

type NothingStockMove struct{}

type DraftStockMove struct {
	Info StockMoveInfo
}

type CompletedStockMove struct {
	Info     StockMoveInfo
	Outgoing m.Quantity
	Incoming m.Quantity
}

type CancelledStockMove struct {
	Info StockMoveInfo
}

type AssignedStockMove struct {
	Info     StockMoveInfo
	Assigned m.Quantity
}

type ShippedStockMove struct {
	Info     StockMoveInfo
	Outgoing m.Quantity
}

type ArrivedStockMove struct {
	Info     StockMoveInfo
	Outgoing m.Quantity
	Incoming m.Quantity
}

type AssignFailedStockMove struct {
	Info StockMoveInfo
}

type ShipmentFailedStockMove struct {
	Info StockMoveInfo
}

type StockMoveResult struct {
	State StockMove
	Event m.StockMoveEvent
}

func (s *NothingStockMove) applyEvent(event m.StockMoveEvent) StockMove {
	switch e := event.(type) {
	case *m.Started:
		info := StockMoveInfo{Item: e.Item, Qty: e.Qty, From: e.From, To: e.To}
		return &DraftStockMove{info}
	}

	return s
}

func (s *DraftStockMove) applyEvent(event m.StockMoveEvent) StockMove {
	switch e := event.(type) {
	case *m.Cancelled:
		return &CancelledStockMove{s.Info}
	case *m.Assigned:
		if e.Assigned > 0 {
			return &AssignedStockMove{s.Info, e.Assigned}
		} else {
			return &AssignFailedStockMove{s.Info}
		}
	case *m.Shipped:
		if e.Outgoing > 0 {
			return &ShippedStockMove{s.Info, e.Outgoing}
		} else {
			return &ShipmentFailedStockMove{s.Info}
		}
	case *m.Arrived:
		var outgoing m.Quantity = 0
		return &ArrivedStockMove{s.Info, outgoing, e.Incoming}
	}

	return s
}

func (s *CompletedStockMove) applyEvent(event m.StockMoveEvent) StockMove {
	return s
}

func (s *CancelledStockMove) applyEvent(event m.StockMoveEvent) StockMove {
	return s
}

func (s *AssignedStockMove) applyEvent(event m.StockMoveEvent) StockMove {
	switch e := event.(type) {
	case *m.AssignShipped:
		if e.Outgoing > 0 {
			return &ShippedStockMove{s.Info, e.Outgoing}

		} else {
			return &ShipmentFailedStockMove{s.Info}
		}
	}

	return s
}

func (s *ShippedStockMove) applyEvent(event m.StockMoveEvent) StockMove {
	switch e := event.(type) {
	case *m.Arrived:
		return &ArrivedStockMove{s.Info, s.Outgoing, e.Incoming}
	}

	return s
}

func (s *ArrivedStockMove) applyEvent(event m.StockMoveEvent) StockMove {
	switch event.(type) {
	case *m.Completed:
		return &CompletedStockMove{s.Info, s.Outgoing, s.Incoming}
	}

	return s
}

func (s *AssignFailedStockMove) applyEvent(event m.StockMoveEvent) StockMove {
	return s
}

func (s *ShipmentFailedStockMove) applyEvent(event m.StockMoveEvent) StockMove {
	return s
}

func Info(state StockMove) (StockMoveInfo, bool) {
	switch s := state.(type) {
	case *DraftStockMove:
		return s.Info, true
	case *CompletedStockMove:
		return s.Info, true
	case *CancelledStockMove:
		return s.Info, true
	case *AssignedStockMove:
		return s.Info, true
	case *ShippedStockMove:
		return s.Info, true
	case *ArrivedStockMove:
		return s.Info, true
	case *AssignFailedStockMove:
		return s.Info, true
	case *ShipmentFailedStockMove:
		return s.Info, true
	}

	return StockMoveInfo{}, false
}

func InitialState() StockMove {
	return &NothingStockMove{}
}

func Action(state StockMove, act StockMoveAction) *StockMoveResult {
	switch a := act.(type) {
	case *Start:
		return start(state, a.Item, a.Qty, a.From, a.To)
	case *Complete:
		return complete(state)
	case *Cancel:
		return cancel(state)
	case *Assign:
		return assign(state, a.Stock)
	case *Ship:
		return ship(state, a.Outgoing)
	case *Arrive:
		return arrive(state, a.Incoming)
	}

	return nil
}

func start(state StockMove, item m.Item, qty m.Quantity,
	from m.Location, to m.Location) *StockMoveResult {

	event := &m.Started{Item: item, Qty: qty, From: from, To: to}
	return apply(state, event)
}

func complete(state StockMove) *StockMoveResult {
	event := &m.Completed{}
	return apply(state, event)
}

func cancel(state StockMove) *StockMoveResult {
	event := &m.Cancelled{}
	return apply(state, event)
}

func assign(state StockMove, stock s.Stock) *StockMoveResult {
	info, ok := Info(state)

	if !ok {
		return nil
	}

	var assigned m.Quantity = 0

	if stock.IsTarget(info.Item, info.From) && stock.IsSufficient(info.Qty) {

		assigned = info.Qty
	}

	event := &m.Assigned{Item: info.Item, From: info.From, Assigned: assigned}
	return apply(state, event)
}

func ship(state StockMove, outgoing m.Quantity) *StockMoveResult {
	info, ok := Info(state)

	if !ok {
		return nil
	}

	s, ok := state.(*AssignedStockMove)

	var event m.StockMoveEvent

	if ok {
		event = &m.AssignShipped{Item: info.Item, From: info.From,
			Outgoing: outgoing, Assigned: s.Assigned}
	} else {
		event = &m.Shipped{Item: info.Item, From: info.From, Outgoing: outgoing}
	}

	return apply(state, event)
}

func arrive(state StockMove, incoming m.Quantity) *StockMoveResult {
	info, ok := Info(state)

	if !ok {
		return nil
	}

	event := &m.Arrived{Item: info.Item, To: info.To, Incoming: incoming}
	return apply(state, event)
}

func apply(state StockMove, event m.StockMoveEvent) *StockMoveResult {
	current := state
	state = state.applyEvent(event)

	if state == current {
		return nil
	}

	return &StockMoveResult{state, event}
}

func Restore(state StockMove, events []m.StockMoveEvent) StockMove {
	s := state

	for _, ev := range events {
		s = s.applyEvent(ev)
	}

	return s
}
