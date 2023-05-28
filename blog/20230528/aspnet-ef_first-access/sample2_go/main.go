package main

import (
	"database/sql"
	"encoding/json"
	"log"
	"net/http"

	_ "github.com/go-sql-driver/mysql"
)

type Item struct {
	ID    string `json:"id"`
	Price int    `json:"price"`
}

type FindItem struct {
	Price int `json:"price"`
}

func main() {
	dsn := "root@tcp(mysql1)/sample"

	db, err := sql.Open("mysql", dsn)

	if err != nil {
		log.Fatal(err)
	}
	defer db.Close()

	http.HandleFunc("/find", func(rw http.ResponseWriter, r *http.Request) {
		var input FindItem
		err := json.NewDecoder(r.Body).Decode(&input)

		if err != nil {
			rw.WriteHeader(http.StatusBadRequest)
		} else {
			rows, err := db.Query(
				"SELECT id, price FROM items WHERE price >= ? ORDER BY price",
				input.Price,
			)

			if err != nil {
				rw.WriteHeader(http.StatusInternalServerError)
				return
			}
			defer rows.Close()

			var (
				res   []Item
				id    string
				price int
			)

			for rows.Next() {
				err := rows.Scan(&id, &price)

				if err == nil {
					res = append(res, Item{id, price})
				}
			}

			json.NewEncoder(rw).Encode(res)
		}
	})

	log.Fatal(http.ListenAndServe(":3000", nil))
}
