import fj.F;
import fj.data.Either;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class EitherDateParse {
    public static void main(String... args) {
        F<String, F<String, Date>> simpleDate = df -> s -> {
            try {
                return new SimpleDateFormat(df).parse(s);
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        };

        Either<String, Date> res = parseDate(
            Either.left(args[0]),
            s -> Date.from(LocalDateTime.parse(s).toInstant(ZoneOffset.UTC)),
            s -> Date.from(OffsetDateTime.parse(s).toInstant()),
            s -> Date.from(ZonedDateTime.parse(s).toInstant()),
            simpleDate.f("yyyy-MM-dd HH:mm:ss"),
            simpleDate.f("yyyy-MM-dd"),
            s -> "now".equals(s)? new Date(): null
        );

        System.out.println("------");

        System.out.println(res);
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
