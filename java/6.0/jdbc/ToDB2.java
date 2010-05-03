
import java.sql.*;

public class ToDB2 {

	public static void main(String[] args) throws Exception {

		Connection con = DriverManager.getConnection("jdbc:db2://localhost:50000/sample", "db2test", "db2testpass");

	//	con.setAutoCommit(false);

		try {
			PreparedStatement ps = con.prepareStatement("SELECT * FROM demo.employee ORDER BY empno");

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				System.out.println("" + rs.getString("empno") + ", " + rs.getString("firstnme"));
			}

			rs.close();
			ps.close();

		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			con.close();
		}
	}
}