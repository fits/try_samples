package main

import (
	"context"
	"fmt"
	"os"

	"github.com/go-git/go-git/v5"
	"github.com/go-git/go-git/v5/plumbing/object"
	"github.com/go-git/go-git/v5/utils/merkletrie"
)

type rename struct {
	from string
	to   string
}

type counter struct {
	commits map[string]uint
	renames []rename
}

func (c *counter) applyChange(d *object.Change) {
	a, _ := d.Action()

	switch a {
	case merkletrie.Insert:
		c.commits[d.To.Name] += 1
	case merkletrie.Delete:
		c.commits[d.From.Name] += 1
	case merkletrie.Modify:
		if d.From.Name == d.To.Name {
			c.commits[d.To.Name] += 1
		} else {
			c.renames = append(c.renames, rename{d.From.Name, d.To.Name})
		}
	}
}

func (c *counter) print() {
	for k, v := range c.commits {
		fmt.Printf("%d\t%s\n", v, k)
	}

	for _, v := range c.renames {
		fmt.Printf("# %s => %s\n", v.from, v.to)
	}
}

func newCounter() counter {
	return counter{make(map[string]uint), make([]rename, 0)}
}

func main() {
	path := os.Args[1]

	r, err := git.PlainOpen(path)

	if err != nil {
		panic(err)
	}

	it, _ := r.Log(&git.LogOptions{All: true})

	counter := newCounter()

	it.ForEach(func(c *object.Commit) error {
		curTree, err := c.Tree()

		if err != nil {
			return err
		}

		var prevTree *object.Tree

		prev, err := c.Parents().Next()

		if err == nil {
			prevTree, _ = prev.Tree()
		}

		chs, _ := object.DiffTreeWithOptions(context.Background(), prevTree, curTree, &object.DiffTreeOptions{DetectRenames: true})

		for _, d := range chs {
			counter.applyChange(d)
		}

		return nil
	})

	counter.print()
}
