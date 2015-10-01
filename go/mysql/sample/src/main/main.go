package main

import (
	"fmt"
	"log"
	"database/sql"
	_ "github.com/go-sql-driver/mysql"
)

func main() {
	db, err := sql.Open("mysql", "root@/sample")

	if err != nil {
		log.Fatal(err)
	}

	defer db.Close()

	rows, err := db.Query("select id, name, price, release_date from product where price > ?", 100)

	if err != nil {
		log.Fatal(err)
	}

	defer rows.Close()

	var (
		id int
		name string
		price int
		date string
	)

	for rows.Next() {
		err := rows.Scan(&id, &name, &price, &date)

		if err != nil {
			log.Fatal(err)
		}

		fmt.Printf("id=%d, name=%s, price=%d, date=%s\n", id, name, price, date)
	}
}

