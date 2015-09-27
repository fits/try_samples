package sample;

import fj.F;
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

public class App7 {
    public static void main(String... args) throws Exception {
        DbState dbs = DbState.reader(args[0]);

        DB<?> q = dbQuery("select count(*) from product", App7::scalarValue);

        System.out.println(dbs.run(q));

        DbState dbw = DbState.writer(args[0]);

        final String insertVariationSql = "insert into product_variation (product_id, color, size) values (?, ?, ?)";

        DB<?> q2 = dbCommand("insert into product (name, price) values (?, ?)",
                    "sample" + System.currentTimeMillis(), 3000)
                .bind(v -> lastInsertId())
                .bind(v -> DB.db(con ->
                    v.bind(r -> r.map(id ->
                            command(insertVariationSql, id, "Pink", "S").f(con)
                                    .bind(_v -> command(insertVariationSql, id, "Yellow", "L").f(con))
                        ).orSome(Validation.<SQLException, Integer>success(0))
                    )
                ));

       System.out.println(dbw.run(q2));
    }

    private static Option<Integer> scalarValue(ResultSet rs) throws SQLException {
        return rs.next() ? Option.some(rs.getInt(1)) : Option.none();
    }

    private static DB<Validation<SQLException, Option<Integer>>> lastInsertId() {
        return dbQuery("select last_insert_id()", App7::scalarValue);
    }

    private static F<Connection, Validation<SQLException, Integer>> command(String sql, Object... params) {
        return Try.f(con -> {
            try (PreparedStatement ps = createStatement(con, sql, params)) {
                return ps.executeUpdate();
            }
        });
    }

    private static DB<Validation<SQLException, Integer>> dbCommand(String sql, Object... params) {
        return DB.db(command(sql, params));
    }

    private static <T> DB<Validation<SQLException, T>> dbQuery(String sql,
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
