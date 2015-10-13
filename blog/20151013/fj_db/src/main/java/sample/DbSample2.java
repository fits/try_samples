package sample;

import fj.Unit;
import fj.control.db.Connector;
import fj.control.db.DB;
import fj.control.db.DbState;
import fj.function.Try1;
import fj.function.TryEffect1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.OptionalInt;

public class DbSample2 {
    public static void main(String... args) throws Exception {
        Connector connector = DbState.driverManager(args[0]);

        final String insertVariationSql = "insert into product_variation (product_id, color, size) " +
                "values (?, ?, ?)";

        DB<Integer> q1 = command("insert into product (name, price) values (?, ?)", "sample2", 2000)
                .bind(v -> query("select last_insert_id()", DbSample2::scalarValue))
                .map(OptionalInt::getAsInt)
                .bind(id ->
                        command(insertVariationSql, id, "Green", "L")
                                .bind(_v -> command(insertVariationSql, id, "Blue", "S")));

        System.out.println(run(connector, q1));

        final String selectSql = "select name, color, size from product p " +
                "join product_variation v on v.product_id = p.id";

        DB<Unit> q2 = query(selectSql, rs -> {
            while (rs.next()) {
                System.out.println(rs.getString("name") + ", " + rs.getString("color") +
                        ", " + rs.getString("size"));
            }
            return Unit.unit();
        });

        runReadOnly(connector, q2);
    }

    private static <A> A runReadOnly(Connector connector, DB<A> dba) throws SQLException {
        return run(connector, dba, Connection::rollback);
    }

    private static <A> A run(Connector connector, DB<A> dba) throws SQLException {
        return run(connector, dba, Connection::commit);
    }

    private static <A> A run(Connector connector, DB<A> dba, TryEffect1<Connection, SQLException> trans)
            throws SQLException {
        try (Connection con = connector.connect()) {
            con.setAutoCommit(false);

            try {
                A result = dba.run(con);

                trans.f(con);

                return result;

            } catch (Throwable e) {
                con.rollback();
                throw e;
            }
        }
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
