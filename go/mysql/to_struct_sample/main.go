package main

import (
	"database/sql"
	"fmt"
	"log"
	"os"
	"time"

	"github.com/blockloop/scan"
	_ "github.com/go-sql-driver/mysql"
)

type Task struct {
	ID        int `db:"id"`
	Name      string
	Status    string
	CreatedAt *time.Time `db:"created_at"`
}

func main() {
	dsn := os.Getenv("MYSQL_DSN")

	db, err := sql.Open("mysql", dsn+"?parseTime=true")

	if err != nil {
		log.Fatal(err)
	}
	defer db.Close()

	rows, err := db.Query(`
		SELECT id, name, status, created_at FROM tasks
	`)

	if err != nil {
		log.Fatal(err)
	}
	defer rows.Close()

	var tasks []Task

	err = scan.Rows(&tasks, rows)

	if err != nil {
		log.Fatal(err)
	}

	for _, t := range tasks {
		fmt.Printf("%#v\n", t)
	}
}
