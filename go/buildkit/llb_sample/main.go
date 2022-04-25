package main

import (
	"context"
	"encoding/json"
	"fmt"

	"github.com/moby/buildkit/client/llb"
	bkpb "github.com/moby/buildkit/solver/pb"
)

func main() {
	st := llb.Image("alpine:3")
	st = st.AddEnv("CODE", "a1")

	def, err := st.Marshal(context.TODO())

	if err != nil {
		panic(err)
	}

	fmt.Printf("%v\n-----\n", def)

	err = dumpLLBOp(def.ToPB())

	if err != nil {
		panic(err)
	}
}

func dumpLLBOp(def *bkpb.Definition) error {
	for _, dt := range def.Def {
		var op bkpb.Op

		if err := (&op).Unmarshal(dt); err != nil {
			return err
		}

		b, _ := json.Marshal(op)

		fmt.Println(string(b))
	}

	return nil
}
