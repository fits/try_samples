package main

import (
	"log"
	"fmt"
	"github.com/golang/protobuf/jsonpb"
	pb "sample"
)

func main() {
	d1 := &pb.Created{ DataId: "d1" }
	d1Ev := &pb.DataEvent_Created{ Created: d1 }

	ev1 := &pb.DataEvent{ EventId: "ev1", Event: d1Ev }

	fmt.Println(ev1)

	d2 := &pb.Updated{ DataId: "d2", Value: 123 }
	d2Ev := &pb.DataEvent_Updated{ Updated: d2 }

	ev2 := &pb.DataEvent{ EventId: "ev2", Event: d2Ev }

	fmt.Println(ev2)

	fmt.Println("--- marshal ---")

	marshaler := &jsonpb.Marshaler{}

	json1, err := marshaler.MarshalToString(ev1)

	if err != nil {
		log.Fatal("ev1 marshal error: ", err)
	} else {
		fmt.Println(json1)
	}

	json2, err := marshaler.MarshalToString(ev2)

	if err != nil {
		log.Fatal("ev2 marshal error: ", err)
	} else {
		fmt.Println(json2)
	}

	fmt.Println("--- unmarshal ---")

	obj1 := &pb.DataEvent{}

	err = jsonpb.UnmarshalString(json1, obj1)

	if err != nil {
		log.Fatal("ev1 unmarshal error: ", err)
	} else {
		fmt.Println(obj1)
	}

	obj2 := &pb.DataEvent{}

	err = jsonpb.UnmarshalString(json2, obj2)

	if err != nil {
		log.Fatal("ev2 unmarshal error: ", err)
	} else {
		fmt.Println(obj2)
	}
}
