SELECT id, name FROM items.jsonl 
WHERE
    list_contains([x.color FOR x IN variants], 'white')