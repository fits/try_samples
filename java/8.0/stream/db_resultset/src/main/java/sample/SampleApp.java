package sample;

import java.sql.*;

public class SampleApp {
	public static void main(String... args) throws Exception {

		try (Connection con = DriverManager.getConnection(args[0])) {
			sample1(con);

			System.out.println("---");

			sample2(con);
		}
	}

	private static void sample1(Connection con) throws SQLException {
		String sql = "select * from product p join product_variation v on v.product_id=p.id";

		try (
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery()
		) {
			new ResultSetSpliterator<>(
				rs,
				r -> r.getString("name") + ", " + r.getString("color") + "," + r.getString("size")
			).stream().forEach(System.out::println);
		}
	}

	private static void sample2(Connection con) throws SQLException {
		String sql = "select * from product p join product_variation v on v.product_id=p.id";

		try (
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery()
		) {
			long count = new ResultSetSpliterator<>(
				rs,
				r -> r.getString("id")
			).stream().count();

			System.out.println("count : " + count);
		}
	}
}
