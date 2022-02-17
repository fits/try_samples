package main

import (
	"bufio"
	"encoding/json"
	"fmt"
	"log"
	"os"
	"strings"
	"time"
)

type Filter struct {
	Field string
	Value string
}

func main() {
	file := os.Args[1]
	query := os.Args[2]

	fs := parseQuery(query)

	t1 := time.Now()

	docs, err := loadLogs(file)

	if err != nil {
		log.Fatal(err)
	}

	t2 := time.Now()

	rs := search(docs, fs)

	t3 := time.Now()

	fmt.Printf("logs size = %d\n", len(docs))
	fmt.Printf("load time = %d ms\n", t2.Sub(t1).Milliseconds())

	fmt.Printf("search logs size = %d\n", len(rs))
	fmt.Printf("search time = %d ms\n", t3.Sub(t2).Milliseconds())
}

func parseQuery(query string) []Filter {
	var fs []Filter

	for _, q := range strings.Split(query, ",") {
		s := strings.Split(q, ":")

		f := Filter{
			strings.TrimSpace(s[0]),
			strings.TrimSpace(s[1]),
		}

		fs = append(fs, f)
	}

	return fs
}

func loadLogs(file string) ([]map[string]interface{}, error) {
	f, err := os.Open(file)

	if err != nil {
		return nil, err
	}
	defer f.Close()

	scanner := bufio.NewScanner(f)

	var rs []map[string]interface{}

	for scanner.Scan() {
		var r map[string]interface{}
		err = json.Unmarshal(scanner.Bytes(), &r)

		if err != nil {
			return nil, err
		}

		rs = append(rs, r)
	}

	return rs, nil
}

func search(docs []map[string]interface{}, fs []Filter) []map[string]interface{} {
	var rs []map[string]interface{}

	for _, d := range docs {
		ok := true

		for _, f := range fs {
			ok = strings.Contains(
				strings.ToLower(fmt.Sprintf("%v", d[f.Field])),
				strings.ToLower(f.Value),
			)

			if !ok {
				break
			}
		}

		if ok {
			rs = append(rs, d)
		}
	}

	return rs
}
