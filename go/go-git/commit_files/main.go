package main

import (
	"context"
	"fmt"
	"log"
	"os"
	"strings"

	"github.com/go-git/go-git/v5"
	"github.com/go-git/go-git/v5/plumbing/object"
)

func main() {
	path := os.Args[1]

	r, err := git.PlainOpen(path)

	if err != nil {
		log.Fatal(err)
	}

	it, err := r.Log(&git.LogOptions{All: true})

	if err != nil {
		log.Fatal(err)
	}

	it.ForEach(func(c *object.Commit) error {
		fmt.Printf("commit hash=%s, message=%s \n", c.Hash.String(), strings.Trim(c.Message, "\n"))

		curTree, err := c.Tree()

		if err != nil {
			return err
		}

		var prevTree *object.Tree

		prev, err := c.Parents().Next()

		if err == nil {
			prevTree, _ = prev.Tree()
		}

		dt, _ := object.DiffTreeWithOptions(context.Background(), prevTree, curTree, &object.DiffTreeOptions{DetectRenames: true})

		for _, d := range dt {
			a, _ := d.Action()

			fmt.Printf("  action=%s, from=%s, to=%s \n", a.String(), d.From.Name, d.To.Name)
		}

		return nil
	})
}
