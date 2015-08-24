package sample;

import fj.control.db.DB;
import fj.control.db.DbState;
import fj.data.List;
import fj.data.Option;
import fj.function.Try1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class App5 {
    public static void main(String... args) throws Exception {
        DbState dbs = DbState.reader(args[0]);

        DB<?> q = query("select count(*) from product", App5::scalarValue);

        System.out.println(dbs.run(q));

        DbState dbw = DbState.writer(args[0]);

        final String insertVariationSql = "insert into product_variation (product_id, color, size) values (?, ?, ?)";

        DB<?> q2 = command("insert into product (name, price) values (?, ?)", "bbb", 4500)
                .bind(v -> query("select last_insert_id()", App5::scalarValue))
                .bind(id ->
                        command(insertVariationSql, id.some(), "Orange", "F")
                                .bind(t -> command(insertVariationSql, id.some(), "Pink", "LL")));

        System.out.println(dbw.run(q2));

        DB<?> q3 = command("insert into product (name, price) values (?, ?)", "ccc", 6200)
                .bind(v -> query("select last_insert_id()", App5::scalarValue))
                .bind(id ->
                        append(
                                command(insertVariationSql, id.some(), "Silver", "F"),
                                command(insertVariationSql, id.some(), "Gold", "F")
                        )
                );

        System.out.println(dbw.run(q3));

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
