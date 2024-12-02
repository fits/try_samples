import duckdb

duckdb.sql("SELECT id, name, attrs.category FROM items.jsonl WHERE attrs.category = 'A1'").show()
