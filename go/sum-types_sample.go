package main

import (
	"fmt"
)

type Status interface {
	IsDone() bool
}

type Ready struct {
	note string
}

type InProgress struct {}

type Completed struct {}

type Canceled struct {
	reason string
}

func (Ready) IsDone() bool {
	return false
}

func (InProgress) IsDone() bool {
	return false
}

func (Completed) IsDone() bool {
	return true
}

func (Canceled) IsDone() bool {
	return true
}

func showStatus(s Status) string {
	switch t := s.(type) {
		case Ready:
			return "ready(" + t.note + ")"
		case InProgress:
			return "inprogress"
		case Completed:
			return "completed"
		case Canceled:
			return "canceled(" + t.reason + ")"
		default:
			return "invalid status"
	}
}

func printStatus(s Status) {
	fmt.Println("staus: ", s, " = ", showStatus(s), ", done: ", s.IsDone())
}

func main() {
	printStatus(Ready{"sample"})
	printStatus(InProgress{})
	printStatus(Completed{})
	printStatus(Canceled{"test"})
}
