import fj.F;
import fj.F3;
import fj.Function;
import fj.P;
import fj.P1;
import fj.Unit;
import fj.data.IO;
import fj.data.Option;
import fj.data.List;
import static fj.data.Iteratee.*;

import java.io.File;
import java.nio.charset.StandardCharsets;

class TakeSample {
	public static void main(String... args) throws Exception {

		IterV<String, List<String>> iter = IterV.<String>drop(1).bind(new F<Unit, IterV<String, List<String>>>() {
			@Override
			public IterV<String, List<String>> f(final Unit xe) {
				return take(3);
			}
		});

		IO<IterV<String, List<String>>> ioIter = IO.enumFileLines(new File(args[0]), Option.some(StandardCharsets.UTF_8), iter);

		ioIter.run().run().foreach(new F<String, Unit>() {
			@Override
			public Unit f(final String line) {
				System.out.println("#" + line);
				return Unit.unit();
			}
		});
	}

	public static final <E> IterV<E, List<E>> take(final int n) {

		final F3<Integer, List<E>, Input<E>, IterV<E, List<E>>> step = new F3<Integer, List<E>, Input<E>, IterV<E, List<E>>>() {

			final F3<Integer, List<E>, Input<E>, IterV<E, List<E>>> step = this;
			@Override
			public IterV<E, List<E>> f(final Integer count, final List<E> acc, final Input<E> s) {
				// Empty 時の処理内容（Input が空の場合）
				final P1<IterV<E, List<E>>> empty = new P1<IterV<E, List<E>>>() {
					@Override
					public IterV<E, List<E>> _1() {
						return IterV.cont(new F<Input<E>, IterV<E, List<E>>>() {
							@Override
							public IterV<E, List<E>> f(Input<E> e) {
								return step.f(count, acc, e);
							}
						});
					}
				};

				// EOF 時の処理内容（終端に達した場合）
				final P1<IterV<E, List<E>>> eof = new P1<IterV<E, List<E>>>() {
					@Override
					public IterV<E, List<E>> _1() {
						return IterV.done(acc, s);
					}
				};

				// El 時の処理内容（Input に値が設定されている場合）
				final P1<F<E, IterV<E, List<E>>>> el = new P1<F<E, IterV<E, List<E>>>>() {
					@Override
					public F<E, IterV<E, List<E>>> _1() {
						return new F<E, IterV<E, List<E>>>() {
							@Override
							public IterV<E, List<E>> f(final E value) {
								if (count <= 0) {
									return IterV.done(acc, s);
								}
								else {
									return IterV.cont(new F<Input<E>, IterV<E, List<E>>>() {
										@Override
										public IterV<E, List<E>> f(Input<E> e) {
											return step.f(count - 1, acc.snoc(value), e);
										}
									});
								}
							}
						};
					}
				};

				return s.apply(empty, el, eof);
			}
		};

		return IterV.cont(new F<Input<E>, IterV<E, List<E>>>() {
			@Override
			public IterV<E, List<E>> f(Input<E> e) {
				return step.f(n, List.<E>nil(), e);
			}
		});
	}
}
