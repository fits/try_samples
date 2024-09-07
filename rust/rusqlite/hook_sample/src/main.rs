use rusqlite::hooks::PreUpdateCase;
use rusqlite::{Connection, Result};

fn main() -> Result<()> {
    let db = Connection::open_in_memory()?;

    db.execute(
        "
        CREATE TABLE item (
            id    INTEGER PRIMARY KEY,
            name  TEXT NOT NULL,
            value INTEGER NOT NULL
        )
        ",
        (),
    )?;

    db.preupdate_hook(Some(|_act, db: &str, tbl: &str, case: &PreUpdateCase| {
        let casestr = match case {
            PreUpdateCase::Insert(v) => format!(
                "insert(row_id={}): id={:?}, name={:?}, value={:?}",
                v.get_new_row_id(),
                v.get_new_column_value(0),
                v.get_new_column_value(1),
                v.get_new_column_value(2),
            ),
            PreUpdateCase::Update {
                old_value_accessor: _,
                new_value_accessor: n,
            } => format!(
                "update(new_row_id={}): name={:?}, value={:?}",
                n.get_new_row_id(),
                n.get_new_column_value(1),
                n.get_new_column_value(2)
            ),
            PreUpdateCase::Delete(v) => format!("delete(row_id={})", v.get_old_row_id()),
            PreUpdateCase::Unknown => "unknown".into(),
        };

        println!("db={}, table={}, case='{}'", db, tbl, casestr);
    }));

    db.execute(
        "INSERT INTO item (name, value) VALUES (?1, ?2)",
        ("item1", 123),
    )?;
    db.execute(
        "INSERT INTO item (name, value) VALUES (?1, ?2)",
        ("item2", 456),
    )?;

    db.execute(
        "UPDATE item SET value = value * 10 WHERE name = ?1",
        ("item1",),
    )?;

    db.execute("DELETE FROM item WHERE name = ?1", ("item2",))?;

    Ok(())
}
