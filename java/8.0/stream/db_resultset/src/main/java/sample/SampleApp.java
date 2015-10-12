package sample;

import java.sql.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class SampleApp {
	public static void main(String... args) throws Exception {

		String sql = "select * from product";

		try (
			Connection con = DriverManager.getConnection(args[0]);
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery()
		) {
			StreamSupport.stream(
					new ResultSetSpliterator<String>(rs,
							r -> r.getString("id") + " : " + r.getString("name")),
					false
			).forEach(System.out::println);
		}
	}
}
