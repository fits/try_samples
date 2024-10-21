package main

import (
	"fmt"
	"os"
	"time"

	"github.com/go-git/go-git/v5"
	"github.com/go-git/go-git/v5/plumbing/object"
	"github.com/go-git/go-git/v5/utils/merkletrie"
)

func main() {
	path := os.Args[1]

	r, err := git.PlainOpen(path)

	if err != nil {
		panic(err)
	}

	head, err := r.Head()

	if err != nil {
		panic(err)
	}

	commit, err := r.CommitObject(head.Hash())

	if err != nil {
		panic(err)
	}

	t := time.Now()

	curTree, _ := commit.Tree()

	var prevTree *object.Tree

	parent, err := commit.Parent(0)

	if err == nil {
		prevTree, _ = parent.Tree()
	}

	changes, _ := prevTree.Diff(curTree)

	fmt.Printf("# tree diff time: %v\n", time.Now().Sub(t))

	for _, d := range changes {
		a, _ := d.Action()

		switch a {
		case merkletrie.Insert:
			fmt.Printf("Add %s\n", d.To.Name)
		case merkletrie.Delete:
			fmt.Printf("Delete %s\n", d.From.Name)
		case merkletrie.Modify:
			if d.From.Name == d.To.Name {
				fmt.Printf("Modify %s\n", d.To.Name)
			} else {
				fmt.Printf("# %s => %s\n", d.From.Name, d.To.Name)
			}
		}
	}
}
