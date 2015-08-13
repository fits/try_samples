package sample;

import java.util.function.Function;

public class App {
    public static void main(String... args) {
        WriterArray<String, Integer> d1 = WriterArray.unit(10);

        System.out.println(d1);

        WriterArray<String, Integer> d2 = WriterArray.unit(10, new String[]{"s1", "s2"});

        System.out.println(d2);

        WriterArray<String, Integer> d3 = d2
                .bind(plus(3, "+3"))
                .bind(plus(2, "+2"))
                .bind(plus(-1, "-1"));

        System.out.println(d3);
    }

    private static Function<Integer, WriterArray<String, Integer>> plus(int value, String msg) {
        return v -> WriterArray.unit(v + value, new String[]{msg});
    }
}
