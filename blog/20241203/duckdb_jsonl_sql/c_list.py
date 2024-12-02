import duckdb

duckdb.sql('''
    SELECT id, name FROM items.jsonl 
    WHERE
        list_contains([x.color FOR x IN variants], 'white')
''').show()

duckdb.sql('''
    SELECT id, name FROM items.jsonl 
    WHERE
        len(list_filter(variants, x -> x.color = 'white')) > 0
''').show()

duckdb.sql('''
    SELECT id, name FROM items.jsonl 
    WHERE
        'white' = ANY ([x.color FOR x in variants])
''').show()
