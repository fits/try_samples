package main

import (
	"fmt"
	"log"
	"os"

	_ "github.com/go-sql-driver/mysql"
	"github.com/jmoiron/sqlx"

	"sample/tasks"
)

func main() {
	dsn := os.Getenv("MYSQL_DSN")

	db, err := sqlx.Connect("mysql", dsn+"?parseTime=true")

	if err != nil {
		log.Fatal(err)
	}
	defer db.Close()

	ts, err := tasks.FindByStatus(db, "ready")

	if err != nil {
		log.Fatal(err)
	}

	for _, t := range ts {
		fmt.Printf("%#v\n", t)
	}
}
