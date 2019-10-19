package main

import (
	"fmt"
	"os"
	"log"
	"github.com/jinzhu/gorm"
	_ "github.com/go-sql-driver/mysql"
)

type Task struct {
	gorm.Model
	Name   string
	Status string `gorm:"type: enum('ready', 'completed'); default: 'ready'; not null"`
}

type TaskService struct {
	db *gorm.DB
}

func (s *TaskService) Create(name string) (uint, error) {
	task := Task{Name: name}
	err := s.db.Create(&task).Error

	if err != nil {
		return 0, err
	}

	return task.ID, nil
}

func (s *TaskService) Complete(id uint) error {
	task, err := s.State(id)

	if err != nil {
		return err
	}

	if task.Status != "ready" {
		return fmt.Errorf("invalid status")
	}

	task.Status = "completed"

	return s.db.Save(&task).Error
}

func (s *TaskService) State(id uint) (*Task, error) {
	var task Task
	
	if err := s.db.First(&task, id).Error; err != nil {
		return nil, err
	}

	return &task, nil
}

func main() {
	dbUrl := os.Getenv("DB_URL")
	db, err := gorm.Open("mysql", dbUrl)

	if err != nil {
		log.Fatalf("oepn error: %v", err)
	}
	defer db.Close()

	db.AutoMigrate(&Task{})

	svc := TaskService{db}

	id, err := svc.Create("sample") 

	if err != nil {
		log.Fatalf("create error: %v", err)
	}

	log.Println("id = ", id)

	d, _ := svc.State(id)

	log.Println("data = ", d)

	if err = svc.Complete(id); err != nil {
		log.Fatalf("complete error: %v", err)
	}

	d, _ = svc.State(id)

	log.Println("data = ", d)
}