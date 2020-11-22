package main

import (
	"fmt"
	"sync"
)

type Data struct {
	value int
}

// (a)
func noLock() {
	var wg sync.WaitGroup

	var ds []Data

	for i := 0; i < 100; i++ {
		wg.Add(1)

		go func() {
			ds = append(ds, Data{i})
			wg.Done()
		}()
	}

	wg.Wait()

	fmt.Println("(a) noLock length =", len(ds))
}

// (b)
func useMutex() {
	var wg sync.WaitGroup
	var mu sync.Mutex

	var ds []Data

	for i := 0; i < 100; i++ {
		wg.Add(1)

		go func() {
			mu.Lock()
			ds = append(ds, Data{i})
			mu.Unlock()

			wg.Done()
		}()
	}

	wg.Wait()

	fmt.Println("(b) useMutex length =", len(ds))
}

// (c)
func useRWMutex() {
	var wg sync.WaitGroup
	var mu sync.RWMutex

	var ds []Data

	for i := 0; i < 100; i++ {
		wg.Add(1)

		go func() {
			mu.Lock()
			ds = append(ds, Data{i})
			mu.Unlock()

			wg.Done()
		}()
	}

	for i := 0; i < 5; i++ {
		wg.Add(1)

		go func() {
			mu.RLock()
			fmt.Println("(c) progress length =", len(ds))
			mu.RUnlock()

			wg.Done()
		}()
	}

	wg.Wait()

	fmt.Println("(c) useRWMutex length =", len(ds))
}

func main() {
	noLock()

	println("-----")

	useMutex()

	println("-----")

	useRWMutex()
}
