import duckdb

duckdb.sql("SELECT id, attrs.code, attrs, variants FROM items.jsonl WHERE id < 4").show()

duckdb.sql('''
    SELECT
        id, name 
    FROM
        items.jsonl 
    WHERE
        list_contains([x.color FOR x IN variants], 'white')
''').show()

duckdb.sql('''
    SELECT
        id, name 
    FROM
        items.jsonl 
    WHERE
        len(list_filter(variants, x -> x.color = 'white')) > 0
''').show()

duckdb.sql('''
    SELECT
        id, name 
    FROM
        items.jsonl 
    WHERE
        'white' = ANY ([x.color FOR x in variants])
''').show()


duckdb.sql('''
    WITH x AS (SELECT id, name, unnest(variants) as v FROM items.jsonl)
    SELECT DISTINCT id, name FROM x WHERE v.color = 'white' 
''').show()

duckdb.sql('''
    SELECT DISTINCT
        id, name
    FROM
        (SELECT id, name, unnest(variants) as v FROM items.jsonl)
    WHERE
        v.color = 'white'
''').show()

# ERROR: Binder Error: Referenced column "color" not found in FROM clause!
#
# duckdb.sql('''
#     SELECT
#         id, name 
#     FROM
#         items.jsonl 
#     WHERE
#         EXISTS (FROM unnest(variants) WHERE color = 'white')
# ''').show()
