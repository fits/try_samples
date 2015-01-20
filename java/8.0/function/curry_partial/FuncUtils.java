
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class FuncUtils {

	public static <T, U, R> Function<T, Function<U, R>> curry(BiFunction<T, U, R> func) {
		return t -> u -> func.apply(t, u);
	}

	public static <S, T, U, R> Function<S, Function<T, Function<U, R>>> curry(F3<S, T, U, R> func) {
		return s -> t -> u -> func.apply(s, t, u);
	}

	public static <Q, S, T, U, R> Function<Q, Function<S, Function<T, Function<U, R>>>> curry(F4<Q, S, T, U, R> func) {
		return q -> s -> t -> u -> func.apply(q, s, t, u);
	}


	public static <T, U> Function<T, Consumer<U>> curry(BiConsumer<T, U> func) {
		return t -> u -> func.accept(t, u);
	}

	public static <S, T, U> Function<S, Function<T, Consumer<U>>> curry(C3<S, T, U> func) {
		return s -> t -> u -> func.accept(s, t, u);
	}

	public static <Q, S, T, U> Function<Q, Function<S, Function<T, Consumer<U>>>> curry(C4<Q, S, T, U> func) {
		return q -> s -> t -> u -> func.accept(q, s, t, u);
	}


	public static <T, U> Function<T, Predicate<U>> curry(BiPredicate<T, U> func) {
		return t -> u -> func.test(t, u);
	}

	public static <S, T, U> Function<S, Function<T, Predicate<U>>> curry(P3<S, T, U> func) {
		return s -> t -> u -> func.test(s, t, u);
	}

	public static <Q, S, T, U> Function<Q, Function<S, Function<T, Predicate<U>>>> curry(P4<Q, S, T, U> func) {
		return q -> s -> t -> u -> func.test(q, s, t, u);
	}


	public static <T, U, R> Function<U, R> partial(BiFunction<T, U, R> func, final T t) {
		return curry(func).apply(t);
	}

	public static <S, T, U, R> Function<T, Function<U, R>> partial(F3<S, T, U, R> func, final S s) {
		return curry(func).apply(s);
	}

	public static <S, T, U, R> Function<U, R> partial(F3<S, T, U, R> func, final S s, final T t) {
		return partial(func, s).apply(t);
	}

	public static <Q, S, T, U, R> Function<S, Function<T, Function<U, R>>> partial(F4<Q, S, T, U, R> func, Q q) {
		return curry(func).apply(q);
	}

	public static <Q, S, T, U, R> Function<T, Function<U, R>> partial(F4<Q, S, T, U, R> func, Q q, S s) {
		return partial(func, q).apply(s);
	}

	public static <Q, S, T, U, R> Function<U, R> partial(F4<Q, S, T, U, R> func, Q q, S s, T t) {
		return partial(func, q, s).apply(t);
	}


	public static <T, U> Consumer<U> partial(BiConsumer<T, U> func, T t) {
		return curry(func).apply(t);
	}

	public static <S, T, U> Consumer<U> partial(C3<S, T, U> func, S s, T t) {
		return partial(func, s).apply(t);
	}

	public static <S, T, U> Function<T, Consumer<U>> partial(C3<S, T, U> func, S s) {
		return curry(func).apply(s);
	}

	public static <Q, S, T, U> Consumer<U> partial(C4<Q, S, T, U> func, Q q, S s, T t) {
		return partial(func, q, s).apply(t);
	}

	public static <Q, S, T, U> Function<T, Consumer<U>> partial(C4<Q, S, T, U> func, Q q, S s) {
		return partial(func, q).apply(s);
	}

	public static <Q, S, T, U> Function<S, Function<T, Consumer<U>>> partial(C4<Q, S, T, U> func, Q q) {
		return curry(func).apply(q);
	}


	public static <T, U> Predicate<U> partial(BiPredicate<T, U> func, T t) {
		return curry(func).apply(t);
	}

	public static <S, T, U> Predicate<U> partial(P3<S, T, U> func, S s, T t) {
		return partial(func, s).apply(t);
	}

	public static <S, T, U> Function<T, Predicate<U>> partial(P3<S, T, U> func, S s) {
		return curry(func).apply(s);
	}

	public static <Q, S, T, U> Predicate<U> partial(P4<Q, S, T, U> func, Q q, S s, T t) {
		return partial(func, q, s).apply(t);
	}

	public static <Q, S, T, U> Function<T, Predicate<U>> partial(P4<Q, S, T, U> func, Q q, S s) {
		return partial(func, q).apply(s);
	}

	public static <Q, S, T, U> Function<S, Function<T, Predicate<U>>> partial(P4<Q, S, T, U> func, Q q) {
		return curry(func).apply(q);
	}



	public static interface F3<S, T, U, R> {
		R apply(S s, T t, U u);
	}

	public static interface F4<Q, S, T, U, R> {
		R apply(Q q, S s, T t, U u);
	}


	public static interface C3<S, T, U> {
		void accept(S s, T t, U u);
	}

	public static interface C4<Q, S, T, U> {
		void accept(Q q, S s, T t, U u);
	}


	public static interface P3<S, T, U> {
		boolean test(S s, T t, U u);
	}

	public static interface P4<Q, S, T, U> {
		boolean test(Q q, S s, T t, U u);
	}
}
