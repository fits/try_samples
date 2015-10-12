package sample;

@FunctionalInterface
public interface TryFunction<T, R, E extends Exception> {
	R apply(T t) throws E;
}
