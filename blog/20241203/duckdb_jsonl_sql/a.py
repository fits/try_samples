import duckdb

duckdb.sql("SELECT id, attrs.code, attrs, variants FROM items.jsonl WHERE id < 4").show()
