import fj.F;
import fj.data.Either;
import fj.data.vector.V;
import fj.data.vector.V2;
import fj.Show;

import org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;

public class EitherDateParse2 {
    public static void main(String... args) {
        F<String, F<String, Date>> simpleDate = df -> s -> {
            try {
                return new SimpleDateFormat(df).parse(s);
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        };

        F<Either<String, Date>, Either<String, Date>> toLowerE = et -> et.left().bind( s -> Either.left(s.toLowerCase()) );

        Either<String, Date> res = parseDate(
            toLowerE.f( Either.left(args[0]) ),
            s -> Date.from(LocalDateTime.parse(s).toInstant(ZoneOffset.UTC)),
            s -> Date.from(OffsetDateTime.parse(s).toInstant()),
            s -> Date.from(ZonedDateTime.parse(s).toInstant()),
            simpleDate.f("yyyy-MM-dd HH:mm:ss"),
            simpleDate.f("yyyy-MM-dd"),
            s -> "now".equals(s)? new Date(): null
        );

        res.left().bind( s -> {
            throw new RuntimeException("failed parse");
        });

        Either<String, V2<Date>> res2 = res.right().bind( d ->
            Either.right(
                V.v(d, DateUtils.truncate(DateUtils.addDays(d, 1), Calendar.DATE))
            )
        );

        System.out.println("------");

        Show.<String, V2<Date>>eitherShow(Show.anyShow(), Show.v2Show(Show.anyShow())).println(res2);
    }


    @SafeVarargs
    public static Either<String, Date> parseDate(Either<String, Date> date, F<String, Date>... funcList) {

        for (F<String, Date> func : funcList) {
            date = date.left().bind( eitherK(func) );
        }
        return date;
    }

    private static <S, T> F<S, Either<S, T>> eitherK(final F<S, T> func) {
        return s -> {
            try {
                T res = func.f(s);
                return (res == null)? Either.left(s): Either.right(res);
            } catch (Exception ex) {
                System.out.println("* " + ex.getMessage());
                return Either.left(s);
            }
        };
    }
}
