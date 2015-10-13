package sample;

import fj.Unit;
import fj.control.db.DB;
import fj.control.db.DbState;
import fj.function.Try1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.OptionalInt;

public class DbSample1 {
    public static void main(String... args) throws Exception {
        DbState dbWriter = DbState.writer(args[0]);

        final String insertVariationSql = "insert into product_variation (product_id, color, size) " +
                "values (?, ?, ?)";

        DB<Integer> q1 = command("insert into product (name, price) values (?, ?)", "sample1", 1500)
                .bind(v -> query("select last_insert_id()", DbSample1::scalarValue))
                .map(OptionalInt::getAsInt)
                .bind(id ->
                        command(insertVariationSql, id, "Black", "L")
                                .bind(_v -> command(insertVariationSql, id, "White", "S")));

        System.out.println(dbWriter.run(q1));

        DbState dbReader = DbState.reader(args[0]);

        final String selectSql = "select name, color, size from product p " +
                "join product_variation v on v.product_id = p.id";

        DB<Unit> q2 = query(selectSql, rs -> {
            while (rs.next()) {
                System.out.println(rs.getString("name") + ", " + rs.getString("color") +
                        ", " + rs.getString("size"));
            }
            return Unit.unit();
        });

        dbReader.run(q2);
    }

    private static OptionalInt scalarValue(ResultSet rs) throws SQLException {
        return rs.next() ? OptionalInt.of(rs.getInt(1)) : OptionalInt.empty();
    }

    private static <A> DB<A> db(Try1<Connection, A, SQLException> func) {
        return new DB<A>() {
            public A run(Connection con) throws SQLException {
                return func.f(con);
            }
        };
    }

    private static DB<Integer> command(String sql, Object... params) {
        return db(con -> {
            try (PreparedStatement ps = createStatement(con, sql, params)) {
                return ps.executeUpdate();
            }
        });
    }

    private static <T> DB<T> query(String sql, Try1<ResultSet, T, SQLException> handler, Object... params) {
        return db(con -> {
            try (
                PreparedStatement ps = createStatement(con, sql, params);
                ResultSet rs = ps.executeQuery()
            ) {
                return handler.f(rs);
            }
        });
    }

    private static PreparedStatement createStatement(Connection con, String sql, Object... params)
            throws SQLException {

        PreparedStatement ps = con.prepareStatement(sql);

        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }

        return ps;
    }
}
