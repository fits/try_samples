package main

import (
	"fmt"
	"log"
	"os"

	_ "github.com/go-sql-driver/mysql"
	"github.com/jmoiron/sqlx"

	"sample/categories"
)

func main() {
	dsn := os.Getenv("MYSQL_DSN")

	db, err := sqlx.Connect("mysql", dsn)

	if err != nil {
		log.Fatal(err)
	}
	defer db.Close()

	cs, err := categories.Find(db)

	if err != nil {
		log.Fatal(err)
	}

	for _, c := range cs {
		printCategory(c, "")
	}
}

func printCategory(c *categories.Category, path string) {
	p := fmt.Sprintf("%s/id=%d,name=%s", path, c.ID, c.Name)
	println(p)

	for _, ch := range c.Children {
		printCategory(ch, p)
	}
}
