SELECT id, name
FROM
    (SELECT id, name, unnest(variants) AS v FROM items.jsonl)
WHERE
    v.color = 'white'