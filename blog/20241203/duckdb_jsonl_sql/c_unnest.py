import duckdb

duckdb.sql('''
    SELECT id, name FROM items.jsonl 
    WHERE
        EXISTS (FROM (SELECT unnest(variants) AS v) WHERE v.color = 'white')
''').show()

duckdb.sql('''
    SELECT DISTINCT id, name
    FROM
        (SELECT id, name, unnest(variants) AS v FROM items.jsonl)
    WHERE
        v.color = 'white'
''').show()

duckdb.sql('''
    WITH x AS (SELECT id, name, unnest(variants) AS v FROM items.jsonl)
    SELECT DISTINCT id, name FROM x WHERE v.color = 'white' 
''').show()

duckdb.sql('''
    SELECT id, name
    FROM
        (SELECT id, name, unnest(variants) AS v FROM items.jsonl)
    WHERE
        v.color = 'white'
''').show()