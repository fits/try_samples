package sample;

import fj.Try;
import fj.control.db.DB;
import fj.control.db.DbState;
import fj.data.Option;
import fj.data.Validation;
import fj.function.Try1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class App4 {
    public static void main(String... args) throws Exception {
        DbState dbs = DbState.reader(args[0]);

        DB<?> q = query("select count(*) from product", App4::scalarValue);

        System.out.println(dbs.run(q));

        DbState dbw = DbState.writer(args[0]);

        DB<?> q2 = command("insert into product (name, price) values (?, ?)", "aaa", 3000)
                .bind(v -> v.map(r -> query("select last_insert_id()", App4::scalarValue)).success());

        System.out.println(dbw.run(q2));
    }

    private static Option<Integer> scalarValue(ResultSet rs) throws SQLException {
        return rs.next() ? Option.some(rs.getInt(1)) : Option.none();
    }

    private static DB<Validation<SQLException, Integer>> command(String sql, Object... params) {
        return DB.db(Try.f(con -> {
            try (PreparedStatement ps = createStatement(con, sql, params)) {
                return ps.executeUpdate();
            }
        }));
    }

    private static <T> DB<Validation<SQLException, T>> query(String sql,
                                                             ResultHandler<T> handler,
                                                             Object... params) {
        return DB.db(Try.f(con -> {
            try (
                PreparedStatement ps = createStatement(con, sql, params);
                ResultSet rs = ps.executeQuery()
            ) {
                return handler.f(rs);
            }
        }));
    }

    private static PreparedStatement createStatement(Connection con, String sql, Object... params)
            throws SQLException {

        PreparedStatement ps = con.prepareStatement(sql);

        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }

        return ps;
    }

    private interface ResultHandler<T> extends Try1<ResultSet, T, SQLException> {
    }
}
