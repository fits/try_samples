package sample;

import com.mysema.query.sql.Configuration;
import com.mysema.query.sql.MySQLTemplates;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.dml.SQLInsertClause;
import sample.model.QProduct;
import sample.model.QVariation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {
	public static void main(String... args) throws SQLException {
		String dbUrl = "jdbc:mysql://localhost:3306/sample1?user=root";

		Configuration conf = new Configuration(new MySQLTemplates());

		QProduct p = QProduct.product;
		QVariation v = QVariation.variation;

		try (Connection con = DriverManager.getConnection(dbUrl)) {
			con.setAutoCommit(false);

			Integer id = new SQLInsertClause(con, conf, p)
					.set(p.name, "test" + System.currentTimeMillis())
					.executeWithKey(p.id);

			new SQLInsertClause(con, conf, v)
					.columns(v.productId, v.size, v.color)
					.values(id, "L", "white")
					.execute();

			new SQLInsertClause(con, conf, v)
					.columns(v.productId, v.size, v.color)
					.values(id, "S", "blue")
					.execute();

			con.commit();

			new SQLQuery(con, conf)
					.from(p)
					.join(v).on(v.productId.eq(p.id))
					.where(p.name.startsWith("test"))
					.list(p.id, p.name, v.color, v.size)
					.forEach(System.out::println);
		}
	}
}