package sample;

import java.sql.*;

public class SampleApp {
	private static final String SQL = "select * from product";

	public static void main(String... args) throws Exception {
		try (Connection con = DriverManager.getConnection(args[0])) {
			sample1(con);

			System.out.println("---");

			sample2(con);
		}
	}

	private static void sample1(Connection con) throws SQLException {
		try (
			PreparedStatement ps = con.prepareStatement(SQL);
			ResultSet rs = ps.executeQuery()
		) {
			new ResultSetSpliterator<>(
				rs,
				r -> r.getString("name")
			).stream().forEach(System.out::println);
		}
	}

	private static void sample2(Connection con) throws SQLException {
		try (
			PreparedStatement ps = con.prepareStatement(SQL);
			ResultSet rs = ps.executeQuery()
		) {
			long count = new ResultSetSpliterator<>(
				rs,
				TryFunction.identity()
			).stream().count();

			System.out.println("count : " + count);
		}
	}
}
