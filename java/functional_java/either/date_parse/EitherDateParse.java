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
        F<String, F<String, Date>> simpleParseDate = f -> s -> {
            try {
                return new SimpleDateFormat(f).parse(s);
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        };

        Either<String, Date> res = parseDate(args[0],
            s -> Date.from(LocalDateTime.parse(s).toInstant(ZoneOffset.UTC)),
            s -> Date.from(OffsetDateTime.parse(s).toInstant()),
            s -> Date.from(ZonedDateTime.parse(s).toInstant()),
            simpleParseDate.f("yyyy-MM-dd HH:mm:ss"),
            simpleParseDate.f("yyyy-MM-dd"),
            s -> {
                if ("now".equals(s)) {
                    return new Date();
                }
                throw new RuntimeException("not now");
            }
        );

        System.out.println("------");

        System.out.println(res);
    }


    @SafeVarargs
    public static Either<String, Date> parseDate(final String date, final F<String, Date>... funcList) {
        Either<String, Date> res = Either.left(date);

        for (F<String, Date> func : funcList) {
            res = res.left().bind( eitherK(func) );
        }
        return res;
    }

    private static <S, T> F<S, Either<S, T>> eitherK(final F<S, T> func) {
        return new F<S, Either<S, T>>() {
            @Override
            public Either<S, T> f(S s) {
                try {
                    return Either.right(func.f(s));
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    return Either.left(s);
                }
            }
        };
    }
}
