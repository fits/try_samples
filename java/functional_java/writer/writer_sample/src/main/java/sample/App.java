package sample;

import fj.F;
import fj.Monoid;
import fj.data.Array;
import fj.data.Writer;

public class App {
    public static void main(String... args) {
        Writer<Array<String>, Integer> data = unit(10, "10");

        System.out.println(data.run());

        Writer<Array<String>, Integer> res = data
                .flatMap(plus(2, "+2"))
                .flatMap(plus(3, "+3"))
                .flatMap(plus(-4, "-4"))
                .flatMap(lift(v -> v * 2));

        System.out.println(res.run());
    }

    private static <A, W> Writer<Array<W>, A> unit(A value, W log) {
        return Writer.unit(value, Array.single(log), Monoid.arrayMonoid());
    }

    private static <A, W> Writer<Array<W>, A> unit(A value) {
        return Writer.unit(value, Monoid.arrayMonoid());
    }

    private static <A, B, W> F<A, Writer<Array<W>, B>> lift(F<A, B> func) {
        return v -> unit(func.f(v));
    }

    private static <A, B, W> F<A, Writer<Array<W>, B>> lift(F<A, B> func, W log) {
        return v -> unit(func.f(v), log);
    }

    private static F<Integer, Writer<Array<String>, Integer>> plus(int value, String msg) {
        return lift(v -> value + v, msg);
    }
}
