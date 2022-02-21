package main

import (
	"bufio"
	"fmt"
	"log"
	"os"
	"strings"
	"time"
)

func main() {
	file := os.Args[1]
	term := os.Args[2]

	t1 := time.Now()

	ds, err := readLines(file)

	if err != nil {
		log.Fatal(err)
	}

	t2 := time.Now()

	rs := filter(ds, term)

	t3 := time.Now()

	fmt.Printf("total = %d, filtered = %d\n", len(ds), len(rs))
	fmt.Printf("read time = %d ms\n", t2.Sub(t1).Milliseconds())
	fmt.Printf("filter time = %d ms\n", t3.Sub(t2).Milliseconds())
}

func readLines(file string) ([]string, error) {
	f, err := os.Open(file)

	if err != nil {
		return nil, err
	}
	defer f.Close()

	scanner := bufio.NewScanner(f)

	var rs []string

	for scanner.Scan() {
		rs = append(rs, scanner.Text())
	}

	return rs, nil
}

func filter(ds []string, term string) []string {
	var rs []string

	for _, d := range ds {
		if strings.Contains(d, term) {
			rs = append(rs, d)
		}
	}

	return rs
}
