package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"net/url"
	"os"
	"strconv"
	"strings"
)

type Document = map[string]interface{}
type Index = map[string]map[string][]int

type QueryFilter struct {
	Field string
	Query string
}

type SearchResult struct {
	Rows  int
	Start int
	Total int
	Docs  []Document
}

type Searcher struct {
	fields []string
	index  Index
	docs   []Document
}

func NewSearcher(fields []string) Searcher {
	return Searcher{
		fields,
		Index{},
		[]Document{},
	}
}

func (s *Searcher) Store(doc Document) {
	id := len(s.docs)

	s.docs = append(s.docs, doc)

	for _, f := range s.fields {
		v, ok := doc[f]

		if ok {
			_, ok = s.index[f]

			if !ok {
				s.index[f] = map[string][]int{}
			}

			switch t := v.(type) {
			case string:
				k := strings.ToLower(t)

				if !containsInt(s.index[f][k], id) {
					s.index[f][k] = append(s.index[f][k], id)
				}
			case []interface{}:
				for _, e := range t {
					k := strings.ToLower(fmt.Sprintf("%v", e))

					if !containsInt(s.index[f][k], id) {
						s.index[f][k] = append(s.index[f][k], id)
					}
				}
			default:
				k := strings.ToLower(fmt.Sprintf("%v", t))

				if !containsInt(s.index[f][k], id) {
					s.index[f][k] = append(s.index[f][k], id)
				}
			}
		}
	}
}

func (s *Searcher) Search(queries []QueryFilter) []Document {
	var tmpIdx []int

	for _, q := range queries {
		if containsStr(s.fields, q.Field) {
			idx := s.index[q.Field][strings.ToLower(q.Query)]

			if tmpIdx == nil || len(idx) < len(tmpIdx) {
				tmpIdx = idx
			}
		}
	}

	var res []Document

	if tmpIdx != nil {
		for _, id := range tmpIdx {
			d := s.docs[id]

			if filter(d, queries) {
				res = append(res, d)
			}
		}
	} else {
		for _, d := range s.docs {
			if filter(d, queries) {
				res = append(res, d)
			}
		}
	}
	return res
}

func filter(doc Document, queries []QueryFilter) bool {
	for _, q := range queries {
		v := doc[q.Field]

		var ok bool

		switch s := v.(type) {
		case string:
			ok = strings.EqualFold(s, q.Query)
		case []interface{}:
			ok = contains(s, strings.ToLower(q.Query))
		default:
			ok = strings.EqualFold(fmt.Sprintf("%v", s), q.Query)
		}

		if !ok {
			return false
		}
	}

	return true
}

func containsInt(vs []int, el int) bool {
	for _, v := range vs {
		if v == el {
			return true
		}
	}

	return false
}

func containsStr(vs []string, el string) bool {
	for _, v := range vs {
		if v == el {
			return true
		}
	}

	return false
}

func contains(vs []interface{}, el string) bool {
	for _, v := range vs {
		switch s := v.(type) {
		case string:
			if strings.EqualFold(s, el) {
				return true
			}
		}
	}

	return false
}

func min(a, b int) int {
	if a < 0 {
		a = 0
	}
	if b < 0 {
		b = 0
	}

	if a < b {
		return a
	}
	return b
}

func toNum(params url.Values, name string, defaultValue int) int {
	v, ok := params[name]

	if !ok {
		return defaultValue
	}

	res, err := strconv.Atoi(v[0])

	if err != nil {
		return defaultValue
	}

	return res
}

func main() {
	var fields []string

	for _, s := range strings.Split(os.Getenv("INDEX_FIELDS"), ",") {
		fields = append(fields, strings.TrimSpace(s))
	}

	sch := NewSearcher(fields)

	http.HandleFunc("/update", func(w http.ResponseWriter, r *http.Request) {
		var docs []map[string]interface{}

		err := json.NewDecoder(r.Body).Decode(&docs)

		if err != nil {
			w.WriteHeader(http.StatusBadRequest)
			fmt.Fprintf(w, "%v", err)
		} else {
			for _, doc := range docs {
				sch.Store(doc)
			}

			w.Header().Add("Content-Type", "application/json")
			w.WriteHeader(http.StatusCreated)
		}
	})

	http.HandleFunc("/search", func(w http.ResponseWriter, r *http.Request) {
		params := r.URL.Query()

		rows := toNum(params, "rows", 10)
		start := toNum(params, "start", 0)

		var qs []QueryFilter

		for _, q := range params["q"] {
			qf := strings.Split(q, ":")

			if len(qf) == 2 {
				qs = append(
					qs,
					QueryFilter{
						strings.TrimSpace(qf[0]),
						strings.TrimSpace(qf[1]),
					},
				)
			}
		}

		docs := sch.Search(qs)

		s := min(len(docs), start)
		e := min(len(docs), s+rows)

		res := SearchResult{rows, start, len(docs), docs[s:e]}

		w.Header().Add("Content-Type", "application/json")
		err := json.NewEncoder(w).Encode(res)

		if err != nil {
			w.WriteHeader(http.StatusInternalServerError)
		}
	})

	log.Fatal(http.ListenAndServe(":8080", nil))
}
