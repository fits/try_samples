package main

import (
    "fmt"
    "os"
    "log"
    "github.com/jinzhu/gorm"
    _ "github.com/go-sql-driver/mysql"
)

type Item struct {
    gorm.Model
    Name string
    Price uint64
}

func main() {
    dbName := os.Getenv("DB_NAME")
    dbUser := os.Getenv("DB_USER")
    dbPass := os.Getenv("DB_PASSWORD")

    dbUrl := fmt.Sprintf("%s:%s@/%s?parseTime=true", dbUser, dbPass, dbName)

    db, err := gorm.Open("mysql", dbUrl)

    if err != nil {
        log.Fatalf("oepn error: %v", err)
    }
    defer db.Close()

    db.AutoMigrate(&Item{})

    db.Create(&Item{Name: "item1", Price: 1000})

    var item Item

    db.First(&item, 1)
    //db.First(&item, "name = ?", "item1")

    log.Println("item: ", item.Name, item.Price)
    log.Println(item)
}