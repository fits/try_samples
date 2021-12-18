package categories

import (
	"database/sql"

	"github.com/jmoiron/sqlx"
)

type categoryRow struct {
	ID       int32 `db:"id"`
	Name     string
	ParentID sql.NullInt32 `db:"parent_id"`
}

type Category struct {
	ID       int32
	Name     string
	Children []*Category
}

func (p *Category) appendChild(ch *Category) {
	p.Children = append(p.Children, ch)
}

var (
	query = `
		SELECT id, name, parent_id FROM categories ORDER BY parent_id
	`
)

func Find(db *sqlx.DB) ([]*Category, error) {
	var rows []categoryRow

	err := db.Select(&rows, query)

	if err != nil {
		return nil, err
	}

	cs := []*Category{}
	ac := map[int32]*Category{}

	for _, r := range rows {
		c := Category{ID: r.ID, Name: r.Name}
		ac[c.ID] = &c

		if r.ParentID.Valid {
			ac[r.ParentID.Int32].appendChild(&c)
		} else {
			cs = append(cs, &c)
		}
	}

	return cs, nil
}
