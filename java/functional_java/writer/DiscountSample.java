import fj.F;
import fj.Monoid;
import fj.data.List;
import fj.data.Writer;

import java.math.BigDecimal;

public class DiscountSample {
	public static void main(String... args) {

		Writer<List<BigDecimal>, BigDecimal> price = unit(
			new BigDecimal("1000")
		);

		show(price);

		show(
			price
				.flatMap(discount("200"))
				.flatMap(discount("30"))
				.flatMap(discount("160"))
		);
	}

	private static Writer<List<BigDecimal>, BigDecimal> unit(BigDecimal value) {
		return Writer.unit(value, Monoid.listMonoid());
	}

	private static Writer<List<BigDecimal>, BigDecimal> unit(BigDecimal value, BigDecimal logValue) {
		return Writer.unit(value, List.single(logValue), Monoid.listMonoid());
	}

	private static F<BigDecimal, Writer<List<BigDecimal>, BigDecimal>> discount(String discountValue) {
		return discount(new BigDecimal(discountValue));
	}

	private static F<BigDecimal, Writer<List<BigDecimal>, BigDecimal>> discount(BigDecimal discountValue) {
		return curValue -> unit(curValue.subtract(discountValue), curValue);
	}

	private static <T> void show(Writer<List<T>, T> trg) {
		System.out.println(trg.run());
	}
}
