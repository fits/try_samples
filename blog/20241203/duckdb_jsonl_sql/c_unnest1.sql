SELECT id, name FROM items.jsonl 
WHERE
    EXISTS (FROM (SELECT unnest(variants) AS v) WHERE v.color = 'white')