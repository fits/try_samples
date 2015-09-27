package sample;

import fj.Try;
import fj.control.db.Connector;
import fj.control.db.DB;
import fj.control.db.DbState;
import fj.data.Option;
import fj.data.Validation;
import fj.function.Try1;
import fj.function.TryEffect1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class App6 {
    public static void main(String... args) throws Exception {
        Connector connector = DbState.driverManager(args[0]);

        DB<?> q = query("select count(*) from product", App6::scalarValue);

        System.out.println(runReadOnly(connector, q));

        final String insertVariationSql = "insert into product_variation (product_id, color, size) values (?, ?, ?)";

        DB<?> q2 = command("insert into product (name, price) values (?, ?)", "app6-" + System.currentTimeMillis(), 3600)
                .bind(v -> lastInsertId())
                .bind(v -> v.map(r -> r.map(id ->
                        command(insertVariationSql, id, "Pink", "S")
                                .bind(_v -> command(insertVariationSql, id, "Yellow", "M"))
                ).some()).success())
                .map(v -> v.success());

        System.out.println(run(connector, q2));
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

    private static Option<Integer> scalarValue(ResultSet rs) throws SQLException {
        return rs.next() ? Option.some(rs.getInt(1)) : Option.none();
    }

    private static DB<Validation<SQLException, Option<Integer>>> lastInsertId() {
        return query("select last_insert_id()", App6::scalarValue);
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
