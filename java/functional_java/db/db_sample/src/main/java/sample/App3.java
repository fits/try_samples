package sample;

import fj.F;
import fj.Try;
import fj.TryEffect;
import fj.control.db.DB;
import fj.control.db.DbState;
import fj.data.List;
import fj.data.Option;
import fj.data.Validation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class App3 {
    public static void main(String... args) throws Exception {
        DbState dbs = DbState.reader(args[0]);

        String sql = "select count(*) from product";

        DB<?> q = select(sql, Try.f(rs ->
                        rs.next() ? Option.some(rs.getInt(1)) : Option.none()));

        System.out.println(dbs.run(q));
    }

    private static <T> DB<Validation<SQLException, T>> select(String sql,
                                               F<ResultSet, Validation<SQLException, T>> resultGen) {
        return select(sql, List.nil(), resultGen);
    }

    private static <T> DB<Validation<SQLException, T>> select(String sql,
                                               List<Object> paramsSet,
                                               F<ResultSet, Validation<SQLException, T>> resultGen) {
        return DB.db(con -> {
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                paramsSet.foldLeft((acc, v) -> {
                    TryEffect.f(() -> ps.setObject(acc, v));
                    return acc + 1;
                }, 1);

                try (ResultSet rs = ps.executeQuery()) {
                    return resultGen.f(rs);
                }
            } catch (SQLException ex) {
                return Validation.fail(ex);
            }
        });
    }
}
