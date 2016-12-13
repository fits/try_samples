
import javaslang.collection.List;

import static javaslang.API.*;
import static javaslang.Predicates.*;

public class App {
    public static void main(String... args) {
        List.range(1, 4).map(App::match).forEach(System.out::println);

        System.out.println("-----");

        List.range(1, 6).map(App::match2).forEach(System.out::println);

    }

    private static String match(int i) {
        return Match(i).of(
                Case(is(1), "one"),
                Case(is(3), "three"),
                Case($(), "etc")
        );
    }

    private static String match2(int i) {
        return Match(i).of(
                Case($(1), "ichi"),
                Case($(3), "san"),
                Case($(n -> n % 2 == 0), "gusu"),
                Case($(), n -> "?" + n)
        );
    }

}
