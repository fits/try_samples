ATTACH 'db/cart.db' AS cart (TYPE SQLITE);

SELECT
    cart_id, item_id, qty, p.title, p.price
FROM
    cart.cart_item AS c
    INNER JOIN (
        SELECT unnest(products) AS p FROM read_json('https://dummyjson.com/products')
    ) ON p.id = c.item_id
;