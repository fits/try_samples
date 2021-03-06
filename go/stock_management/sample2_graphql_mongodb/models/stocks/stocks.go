package stocks

import m "sample/models"

type Stock interface {
	IsTarget(item m.Item, location m.Location) bool
	IsSufficient(qty m.Quantity) bool
	applyEvent(event m.StockMoveEvent) Stock
}

type UnmanagedStock struct {
	Item     m.Item
	Location m.Location
}

type ManagedStock struct {
	Item     m.Item
	Location m.Location
	Qty      m.Quantity
	Assigned m.Quantity
}

func (s *UnmanagedStock) IsTarget(item m.Item, location m.Location) bool {
	return s.Item == item && s.Location == location
}

func (_ *UnmanagedStock) IsSufficient(qty m.Quantity) bool {
	return true
}

func (s *UnmanagedStock) applyEvent(event m.StockMoveEvent) Stock {
	return s
}

func (s *ManagedStock) IsTarget(item m.Item, location m.Location) bool {
	return s.Item == item && s.Location == location
}

func (s *ManagedStock) IsSufficient(qty m.Quantity) bool {
	return s.Qty >= s.Assigned+qty
}

func (s *ManagedStock) applyEvent(event m.StockMoveEvent) Stock {
	switch ev := event.(type) {
	case *m.Assigned:
		if s.IsTarget(ev.Item, ev.From) {
			return s.updateAssigned(ev.Assigned + s.Assigned)
		}
	case *m.AssignShipped:
		if s.IsTarget(ev.Item, ev.From) {
			var q, a m.Quantity

			if s.Qty > ev.Outgoing {
				q = s.Qty - ev.Outgoing
			}
			if s.Assigned > ev.Assigned {
				a = s.Assigned - ev.Assigned
			}

			return s.update(q, a)
		}
	case *m.Shipped:
		if s.IsTarget(ev.Item, ev.From) {
			var q m.Quantity

			if s.Qty > ev.Outgoing {
				q = s.Qty - ev.Outgoing
			}

			return s.updateQty(q)
		}
	case *m.Arrived:
		if s.IsTarget(ev.Item, ev.To) {
			return s.updateQty(s.Qty + ev.Incoming)
		}
	}

	return s
}

func (s *ManagedStock) updateQty(qty m.Quantity) Stock {
	return s.update(qty, s.Assigned)
}

func (s *ManagedStock) updateAssigned(assigned m.Quantity) Stock {
	return s.update(s.Qty, assigned)
}

func (s *ManagedStock) update(qty m.Quantity, assigned m.Quantity) Stock {
	if s.Qty == qty && s.Assigned == assigned {
		return s
	}

	return &ManagedStock{
		s.Item,
		s.Location,
		qty,
		assigned,
	}
}

func InitialUnmanaged(item m.Item, location m.Location) Stock {
	return &UnmanagedStock{item, location}
}

func InitialManaged(item m.Item, location m.Location) Stock {
	return &ManagedStock{item, location, 0, 0}
}

func RestoreStock(state Stock, events []m.StockMoveEvent) Stock {
	s := state

	for _, ev := range events {
		s = s.applyEvent(ev)
	}

	return s
}
