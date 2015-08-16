package sample;

import fj.F;
import fj.Try;
import fj.Unit;
import fj.control.db.DB;
import fj.control.db.DbState;
import fj.data.Option;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class App2 {
    public static void main(String... args) throws Exception {
        DbState dbs = DbState.reader(args[0]);

        String sql = "select count(*) from product";

        DB<?> q = DB.db(select(sql, Try.f(rs ->
            rs.next() ? Option.some(rs.getInt(1)) : Option.none()
        )));

        System.out.println(dbs.run(q));
    }

    private static <T> F<Connection, T> select(String sql,
                                               F<ResultSet, T> result) {
        return select(sql, ps -> Unit.unit(), result);
    }

    private static <T> F<Connection, T> select(String sql,
                                               F<PreparedStatement, Unit> paramsSet,
                                               F<ResultSet, T> resultGen) {
        return con -> {
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                paramsSet.f(ps);

                try (ResultSet rs = ps.executeQuery()) {
                    return resultGen.f(rs);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        };
    }
}
