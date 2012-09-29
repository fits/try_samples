import fj.*;
import fj.data.*;
import static fj.P.*;
import static fj.Show.*;
import static fj.data.List.*;

/**
 * OpenJDK build 1.8.0-ea-lambda-nightly-h1171-20120911-b56-b00
 *
 */
class MoveKnightMany {
	public static void main(String[] args) {
		// KnightPos 用の Show 作成
		Show<KnightPos> ks = show(
			f(
				(KnightPos p) -> Stream.fromString("(" + p._1() + ", " + p._2() + ")")
			)
		);

		listShow(ks).println( inMany(3, pos(6, 2)) );

		booleanShow.println( canReachInMany(3, pos(6, 2), pos(6, 1)) );
		booleanShow.println( canReachInMany(3, pos(6, 2), pos(7, 3)) );
	}

	// ナイトの次の移動先を列挙
	static F<KnightPos, List<KnightPos>> moveKnight() {
		return f(
			(KnightPos p) -> list(
				pos(p._1() + 2, p._2() - 1), pos(p._1() + 2, p._2() + 1),
				pos(p._1() - 2, p._2() - 1), pos(p._1() - 2, p._2() + 1),
				pos(p._1() + 1, p._2() - 2), pos(p._1() + 1, p._2() + 2),
				pos(p._1() - 1, p._2() - 2), pos(p._1() - 1, p._2() + 2)
			).filter(
				f(
					(KnightPos t) -> 1<= t._1() && t._1() <= 8 && 1 <= t._2() && t._2() <= 8
				)
			)
		);
	}

	// x手先の移動位置を列挙
	static List<KnightPos> inMany(int x, KnightPos start) {
		return list(start).bind(
			replicate(x, moveKnight()).foldLeft1(
				f2(
					(F<KnightPos, List<KnightPos>> b, F<KnightPos, List<KnightPos>> a) -> f( (KnightPos n) -> b.f(n).bind(a) )
				)
			)
		);
	}

	// 指定位置にx手で到達できるか否かを判定
	static boolean canReachInMany(int x, KnightPos start, KnightPos end) {
		return inMany(x, start).exists(
			f(
				(KnightPos p) -> p._1() == end._1() && p._2() == end._2()
			)
		);
	}

	// fj.F のインスタンスを生成
	static <A, B> F<A, B> f(final Func<A, B> func) {
		return new F<A, B>() {
			@Override
			public B f(A a) {
				return func.apply(a);
			}
		};
	}

	static <A, B, C> F2<A, B, C> f2(final Func2<A, B, C> func) {
		return new F2<A, B, C>() {
			@Override
			public C f(A a, B b) {
				return func.apply(a, b);
			}
		};
	}

	static KnightPos pos(int x, int y) {
		return new KnightPos(x, y);
	}

	interface Func<A, B> {
		B apply(A a);
	}

	interface Func2<A, B, C> {
		C apply(A a, B b);
	}

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
}
