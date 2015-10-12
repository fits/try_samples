package sample;

import java.sql.*;

public class SampleApp {
	public static void main(String... args) throws Exception {

		String sql = "select * from product";

		try (
			Connection con = DriverManager.getConnection(args[0]);
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery()
		) {
			new ResultSetSpliterator<String>(
					rs,
					r -> r.getString("id") + " : " + r.getString("name")
			).stream().forEach(System.out::println);
		}
	}
}
