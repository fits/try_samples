package tasks

import (
	"time"

	"github.com/jmoiron/sqlx"
)

type Task struct {
	ID        int `db:"id"`
	Name      string
	Status    string
	CreatedAt *time.Time `db:"created_at"`
}

var (
	sql = `
		SELECT id, name, status, created_at FROM tasks WHERE status = :status
	`
)

func FindByStatus(db *sqlx.DB, status string) ([]Task, error) {
	stmt, err := db.PrepareNamed(sql)

	if err != nil {
		return nil, err
	}
	defer stmt.Close()

	var tasks []Task

	err = stmt.Select(&tasks, map[string]interface{}{"status": status})

	if err != nil {
		return nil, err
	}

	return tasks, nil
}
