package main

import (
	"fmt"

	"go.mongodb.org/mongo-driver/bson"

	"sample/models"
)

type StoredEvent struct {
	Started       *models.Started       `bson:",omitempty"`
	Completed     *models.Completed     `bson:",omitempty"`
	Cancelled     *models.Cancelled     `bson:",omitempty"`
	Assigned      *models.Assigned      `bson:",omitempty"`
	AssignShipped *models.AssignShipped `bson:",omitempty"`
	Shipped       *models.Shipped       `bson:",omitempty"`
	Arrived       *models.Arrived       `bson:",omitempty"`
}

func main() {
	ev1 := StoredEvent{Assigned: &models.Assigned{"item-1", "from-1", 2}}

	buf, err := bson.MarshalExtJSON(ev1, false, false)

	if err != nil {
		panic(err)
	}

	fmt.Println(string(buf))

	var ev2 StoredEvent

	err = bson.UnmarshalExtJSON(buf, false, &ev2)

	if err != nil {
		panic(err)
	}

	fmt.Printf("*** restored: %#v\n", ev2)

	if ev2.Assigned != nil {
		fmt.Printf(
			"*** assigned item=%s, from=%s, assigned=%d\n", 
			ev2.Assigned.Item, ev2.Assigned.From, ev2.Assigned.Assigned,
		)
	}
}
