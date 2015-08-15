package sample;

import fj.F;
import fj.control.db.DB;
import fj.control.db.DbState;
import fj.data.Option;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class App {
    public static void main(String... args) throws Exception {
        DbState dbs = DbState.reader(args[0]);

        String sql = "select count(*) from product";

        F<ResultSet, ?> firstRow = rs -> tryGet(() -> rs.next()?
                Option.some(rs.getObject(1)): Option.none());

        DB<?> q = DB.db(con -> statement(sql, con)).bind(ps ->
                DB.unit(resultSet(ps)).bind(rs ->
                        DB.unit(firstRow.f(rs)).map(res -> {
                            tryCall(rs::close);
                            tryCall(ps::close);
                            return res;
                        })));

        System.out.println(dbs.run(q));
    }

    private static PreparedStatement statement(String sql, Connection con) {
        return tryGet(() -> con.prepareStatement(sql));
    }

    private static ResultSet resultSet(PreparedStatement ps) {
        return tryGet(ps::executeQuery);
    }

    private static <T> T tryGet(ExceptionSupplier<T, ?> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void tryCall(ExceptionCaller<?> caller) {
        try {
            caller.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    private interface ExceptionSupplier<T, E extends Exception> {
        T get() throws E;
    }

    @FunctionalInterface
    private interface ExceptionCaller<E extends Exception> {
        void call() throws E;
    }
}
