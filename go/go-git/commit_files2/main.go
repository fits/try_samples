package main

import (
	"context"
	"fmt"
	"os"
	"sync"

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

	it, _ := r.Log(&git.LogOptions{All: true})

	var wg sync.WaitGroup

	it.ForEach(func(c *object.Commit) error {
		wg.Add(1)

		go func() {
			defer wg.Done()

			curTree, err := c.Tree()

			if err != nil {
				return
			}

			var prevTree *object.Tree

			prev, err := c.Parents().Next()

			if err == nil {
				prevTree, _ = prev.Tree()
			}

			chs, _ := object.DiffTreeWithOptions(context.Background(), prevTree, curTree, &object.DiffTreeOptions{DetectRenames: true})

			for _, d := range chs {
				a, _ := d.Action()

				switch a {
				case merkletrie.Insert:
					fmt.Println(d.To.Name)
				case merkletrie.Delete:
					fmt.Println(d.From.Name)
				case merkletrie.Modify:
					if d.From.Name == d.To.Name {
						fmt.Println(d.To.Name)
					} else {
						fmt.Printf("# %s => %s\n", d.From.Name, d.To.Name)
					}
				}
			}
		}()

		return nil
	})

	wg.Wait()
}
