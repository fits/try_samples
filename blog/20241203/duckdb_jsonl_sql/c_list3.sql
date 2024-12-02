SELECT id, name FROM items.jsonl 
WHERE
    'white' = ANY ([x.color FOR x in variants])