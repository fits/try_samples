import fj.*;
import fj.data.List;
import static fj.P.*;
import static fj.Show.*;
import static fj.data.List.*;

/**
 * OpenJDK build 1.8.0-ea-lambda-nightly-h1171-20120911-b56-b00 の
 * JSR-335 を使って実装した Functional Java のサンプル
 *
 */
class MoveKnight {
	public static void main(String[] args) {
		listShow(p2Show(intShow, intShow)).println(moveKnight(pos(6, 2)));

	}

	static List<P2<Integer, Integer>> moveKnight(P2<Integer, Integer> p) {
		return list(
			pos(p._1() + 2, p._2() - 1), pos(p._1() + 2, p._2() + 1),
			pos(p._1() - 2, p._2() - 1), pos(p._1() - 2, p._2() + 1),
			pos(p._1() + 1, p._2() - 2), pos(p._1() + 1, p._2() + 2),
			pos(p._1() - 1, p._2() - 2), pos(p._1() - 1, p._2() + 2)
		).filter(f( (P2<Integer, Integer> t) ->
			1<= t._1() && t._1() <= 8 && 1 <= t._2() && t._2() <= 8
		));
	}

	// fj.F のインスタンスを生成するメソッド
	static <A, B> F<A, B> f(final Func<A, B> func) {
		return new F<A, B>() {
			@Override
			public B f(A a) {
				return func.apply(a);
			}
		};
	}

	static P2<Integer, Integer> pos(int x, int y) {
		return p(x, y);
		//return new KnightPos(x, y);
	}

	// ラムダ式が適用されるインターフェース
	interface Func<A, B> {
		B apply(A a);
	}
/*
	static class KnightPos extends P2<Integer, Integer> {
		private int x;
		private int y;

		KnightPos(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override public Integer _1() { return x; }
		@Override public Integer _2() { return y; }
	}
*/
}
