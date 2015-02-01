package sample;

import com.mysema.query.sql.Configuration;
import com.mysema.query.sql.MySQLTemplates;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.types.Projections;
import sample.model.QProduct;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SampleApp {
    public static void main(String... args) throws SQLException {
        Configuration conf = new Configuration(new MySQLTemplates());

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/sample?user=root")) {
            QProduct q = QProduct.product;

            new SQLQuery(con, conf)
                    .from(q)
                    .where(q.price.between(new BigDecimal("500"), new BigDecimal("2500")))
                    .list(q.all())
                    .forEach(System.out::println);

            System.out.println("-----");

            new SQLQuery(con, conf)
                    .from(q)
                    .where(q.price.lt(new BigDecimal("2500")))
                    .list(q.name, q.price)
                    .forEach(p -> {
						System.out.println(p.get(q.name) + ", " + p.get(q.price));
					});

            System.out.println("-----");

            new SQLQuery(con, conf)
                    .from(q)
                    .where(q.price.lt(new BigDecimal("2500")).or(q.price.eq(new BigDecimal("2500"))))
					.orderBy(q.name.desc())
                    // mapping to Product
                    .list(Projections.bean(Product.class, q.name, q.price))
                    .forEach(p -> {
						System.out.println(p + ", " + p.getName() + ", " + p.getPrice());
					});
        }
    }

    public static class Product {
        private String name;
        private BigDecimal price;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }
}
