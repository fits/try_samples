package sample;

import java.util.function.Function;

public class Sample {
	public static <S, T> T exec(Function<S, T> func, S s) {
		return func.apply(s);
	}
}