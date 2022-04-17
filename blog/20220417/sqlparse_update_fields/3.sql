UPDATE ITEMS SET price = 1000 WHERE id = '1';
SELECT * FROM sample.ITEMS WHERE price > 1000;

update
  sample.ITEMS as i 
set
  i.price = 200, 
  i.updated_at = NOW(), 
  i.rev = i.rev + 1
where
  id = '2';

delete from ITEMS where price <= 0;
