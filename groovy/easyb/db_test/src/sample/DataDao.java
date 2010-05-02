package sample;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataDao {

	private Connection con;

	public DataDao(Connection con) {
		this.con = con;
	}

	public List<Data> searchWithName(String name) {
		ArrayList<Data> result = new ArrayList<Data>();

		try {
			PreparedStatement ps = this.con.prepareStatement("select * from data where name=?");
			ps.setString(1, name);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Data data = new Data();
				data.id = rs.getInt("id");
				data.name = rs.getString("name");
				data.point = rs.getInt("point");

				result.add(data);
			}
		}
		catch (SQLException ex) {
		}

		return result;
	}
}