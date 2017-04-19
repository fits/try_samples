
import javaslang.collection.List;

import static javaslang.API.*;
import static javaslang.Predicates.*;

public class SampleApp {
    public static void main(String... args) {
        List.range(1, 4).map(SampleApp::match).forEach(System.out::println);
    }

    private static String match(int i) {
        return Match(i).of(
            Case(is(1), "one"),
            Case(is(3), "three"),
            Case($(), "etc")
        );
    }
}