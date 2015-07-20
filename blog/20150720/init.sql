
CREATE TABLE `product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  `price` decimal(10,0) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `product_variation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) NOT NULL DEFAULT 0,
  `color` varchar(10) NOT NULL,
  `size` varchar(10) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into product (name, price) values ('test1', 100);
insert into product_variation (product_id, size, color) values (1, 'F', 'Green');
insert into product_variation (product_id, size, color) values (1, 'S', 'Blue');

insert into product (name, price) values ('test2', 200);
insert into product_variation (product_id, size, color) values (2, 'S', 'Red');
