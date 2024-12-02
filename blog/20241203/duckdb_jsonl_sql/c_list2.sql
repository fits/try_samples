SELECT id, name FROM items.jsonl 
WHERE
    len(list_filter(variants, x -> x.color = 'white')) > 0