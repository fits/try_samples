
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.function.Function;

public class DateParse {
    public static void main(String... args) {
        Function<String, Function<String, Date>> simpleDate = df -> s -> {
            try {
                return new SimpleDateFormat(df).parse(s);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        Date res = parseDate(
            args[0],
            s -> Date.from(LocalDateTime.parse(s).toInstant(ZoneOffset.UTC)),
            s -> Date.from(OffsetDateTime.parse(s).toInstant()),
            s -> Date.from(ZonedDateTime.parse(s).toInstant()),
            simpleDate.apply("yyyy-MM-dd HH:mm:ss"),
            s -> "now".equals(s)? new Date(): null
        );

        System.out.println("------");

        System.out.println(res);
    }


    @SafeVarargs
    public static Date parseDate(String date, Function<String, Date>... funcList) {
        for (Function<String, Date> func : funcList) {
            try {
                Date res = func.apply(date);

                if (res != null) {
                    return res;
                }
            } catch (Exception ex) {
                System.out.println("* " + ex.getMessage());
            }
        }
        return null;
    }
}
