package sample;

import fj.control.db.DB;
import fj.control.db.DbState;
import fj.data.Validation;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;

public class App {
    public static void main(String... args) throws Exception {
        DbState dbs = DbState.reader(args[0]);

        QueryRunner runner = new QueryRunner();

        String sql = "select count(*) from product";

        DB<?> q = query(runner, sql, new ScalarHandler<Long>(1));

        System.out.println(dbs.run(q));
    }

    private static <T> DB<Validation<SQLException, T>> query(QueryRunner runner,
                                                             String sql,
                                                             ResultSetHandler<T> rsh,
                                                             Object... params) {
        return DB.db(con -> {
            try {
                return Validation.success(runner.query(con, sql, rsh, params));
            } catch (SQLException e) {
                return Validation.fail(e);
            }
        });
    }
}
