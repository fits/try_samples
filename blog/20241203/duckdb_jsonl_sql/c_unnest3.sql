WITH x AS (SELECT id, name, unnest(variants) AS v FROM items.jsonl)
SELECT DISTINCT id, name FROM x WHERE v.color = 'white'