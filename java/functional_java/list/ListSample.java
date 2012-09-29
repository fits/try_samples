import fj.F;
import fj.data.List;
import static fj.data.List.*;

/**
 * OpenJDK build 1.8.0-ea-lambda-nightly-h1171-20120911-b56-b00 の
 * JSR-335 を使って実装した Functional Java のサンプル
 *
 */
class ListSample {
	public static void main(String[] args) {

		List data = list("a1", "a2", "b1");

		List res = data.map(f((String s) -> "*" + s + "*"));

		res.foreach(f((String s) -> {
			System.out.println(s);
			return null;
		}));


		F threeF = f((String s) -> list(s + "-1", s + "-2", s + "-3"));

		System.out.println("-------");
		res.bind(threeF).bind(threeF).foreach(f((String s) -> {
			System.out.println(s);
			return null;
		}));
	}

	// fj.F のインスタンスを生成するメソッド
	public static <A, B> F<A, B> f(final Func<A, B> func) {
		return new F<A, B>() {
			@Override
			public B f(A a) {
				return func.apply(a);
			}
		};
	}

	// ラムダ式が適用されるインターフェース
	interface Func<A, B> {
		B apply(A a);
	}
}
