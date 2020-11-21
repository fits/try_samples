package main

import (
	"fmt"
	"sync"
)

type Data struct {
	value int
}

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

	fmt.Println("noLock length =", len(ds))
}

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

	fmt.Println("useMutex length =", len(ds))
}

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

	for i := 0; i < 3; i++ {
		wg.Add(1)

		go func() {
			mu.RLock()
			fmt.Println("progress length =", len(ds))
			mu.RUnlock()

			wg.Done()
		}()
	}

	wg.Wait()

	fmt.Println("useRWMutex length =", len(ds))
}

func main() {
	noLock()

	println("-----")

	useMutex()

	println("-----")

	useRWMutex()
}
