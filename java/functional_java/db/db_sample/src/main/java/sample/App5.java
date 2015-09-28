package sample;

import fj.control.db.Connector;
import fj.control.db.DB;
import fj.control.db.DbState;
import fj.data.List;
import fj.data.Option;
import fj.function.Try1;
import fj.function.TryEffect1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class App5 {
    public static void main(String... args) throws Exception {
        Connector connector = DbState.driverManager(args[0]);

        DB<?> q = query("select count(*) from product", App5::scalarValue);

        System.out.println(runReadOnly(connector, q));

        final String insertVariationSql = "insert into product_variation (product_id, color, size) values (?, ?, ?)";

        DB<?> q2 = command("insert into product (name, price) values (?, ?)", "bbb", 4500)
                .bind(v -> query("select last_insert_id()", App5::scalarValue))
                .map(v -> v.some())
                .bind(id ->
                        command(insertVariationSql, id, "Orange", "F")
                                .bind(_v -> command(insertVariationSql, id, "Pink", "LL")));

        System.out.println(run(connector, q2));

        DB<?> q3 = command("insert into product (name, price) values (?, ?)", "ccc", 6200)
                .bind(v -> query("select last_insert_id()", App5::scalarValue))
                .map(v -> v.some())
                .bind(id ->
                        append(
                                command(insertVariationSql, id, "Silver", "F"),
                                command(insertVariationSql, id, "Gold", "F")
                        )
                );

        System.out.println(run(connector, q3));

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

    private static Option<Integer> scalarValue(ResultSet rs) {
        try {
            return rs.next() ? Option.some(rs.getInt(1)) : Option.none();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static DB<Integer> command(String sql, Object... params) {
        return DB.db(con -> {
            try (PreparedStatement ps = createStatement(con, sql, params)) {
                return ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static <T> DB<T> query(String sql, ResultHandler<T> handler, Object... params) {
        return DB.db(con -> {
            try (
                PreparedStatement ps = createStatement(con, sql, params);
                ResultSet rs = ps.executeQuery()
            ) {
                return handler.f(rs);
            } catch (SQLException e) {
                throw new RuntimeException(e);
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

    @SafeVarargs
    private static <T> DB<List<T>> append(DB<T>... dbs) {
        return DB.db(con -> List.list(dbs).map(db -> {
            try {
                return db.run(con);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    private interface ResultHandler<T> extends Try1<ResultSet, T, SQLException> {
    }
}
