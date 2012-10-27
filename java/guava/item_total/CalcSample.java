
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import static com.google.common.collect.Collections2.*;

public class CalcSample {
	public static void main(String[] args) {

		List<ProductItem> items = Arrays.asList(
			new ProductItem("id1", "商品1", new BigDecimal("1000"), 1),
			new ProductItem("id2", "商品2", new BigDecimal("3000"), 2),
			new ProductItem("id3", "商品3", new BigDecimal("1500"), 3)
		);

		Collection<String> names = transform(items, new Function<ProductItem, String>() {
			public String apply(ProductItem it) {
				return it.getName();
			}
		});

		for (String n : names) {
			System.out.println(n);
		}

		System.out.println("-----");

		Collection<ProductItem> highItems = filter(items, new Predicate<ProductItem>() {
			public boolean apply(ProductItem it) {
				return it.getPrice().compareTo(new BigDecimal("1500")) >= 0;
			}
		});

		for (ProductItem it : highItems) {
			System.out.println(it.getName());
		}

		System.out.println("-----");

		// 合計金額算出
		BigDecimal total = foldLeft(items, new F2<BigDecimal, ProductItem, BigDecimal>() {
				public BigDecimal f(BigDecimal a, ProductItem b) {
					return a.add(b.getPrice().multiply(new BigDecimal(b.getQty())));
				}
			},
			BigDecimal.ZERO
		);

		System.out.println(total);
	}

	private static <S, T> T foldLeft(Collection<S> collection, F2<T, S, T> func, T value) {
		for (S item : collection) {
			value = func.f(value, item);
		}
		return value;
	}

	interface F2<A, B, C> {
		C f(A a, B b);
	}
}

