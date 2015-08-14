package sample;

import fj.control.db.DB;
import fj.control.db.DbState;
import fj.data.Option;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class App {
    public static void main(String... args) throws Exception {
        String url = "jdbc:mysql://localhost:3306/jpa_sample?user=root";

        DbState dbs = DbState.reader(url);

        String sql = "select count(*) from product";

        DB<?> q = DB.db(con -> statement(sql, con)).bind(ps ->
                DB.unit(resultSet(ps)).bind(rs ->
                        DB.unit(headFirstColumn(rs)).map(res -> {
                            close(rs);
                            close(ps);
                            return res;
                        })));

        System.out.println(dbs.run(q));
    }

    private static PreparedStatement statement(String sql, Connection con) {
        try {
            return con.prepareStatement(sql);
        } catch (SQLException e) {
            throw new RuntimeException("", e);
        }
    }

    private static ResultSet resultSet(PreparedStatement ps) {
        try {
            return ps.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("", e);
        }
    }

    private static Option<Object> headFirstColumn(ResultSet rs) {
        try {
            return rs.next()? Option.some(rs.getObject(1)): Option.none();
        } catch (SQLException e) {
            throw new RuntimeException("", e);
        }
    }

    private static void close(AutoCloseable trg) {
        try {
            trg.close();
        } catch (Exception e) {
            throw new RuntimeException("", e);
        }
    }
}
