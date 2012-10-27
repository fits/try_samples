
import java.math.BigDecimal;

import com.googlecode.totallylazy.*;
import static com.googlecode.totallylazy.Sequences.*;

public class CalcSample {
	public static void main(String[] args) {

		Sequence<ProductItem> items = sequence(
			new ProductItem("id1", "商品1", new BigDecimal("1000"), 1),
			new ProductItem("id2", "商品2", new BigDecimal("3000"), 2),
			new ProductItem("id3", "商品3", new BigDecimal("1500"), 3)
		);

		Sequence<String> names = items.map(new Callable1<ProductItem, String>() {
			public String call(ProductItem it) {
				return it.getName();
			}
		});

		names.each(new Callable1<String, Void>() {
			public Void call(String n) {
				System.out.println(n);
				return null;
			}
		});

		System.out.println("-----");

		Sequence<ProductItem> highItems = items.filter(new Predicate<ProductItem>() {
			public boolean matches(ProductItem it) {
				return it.getPrice().compareTo(new BigDecimal("1500")) >= 0;
			}
		});

		highItems.each(new Callable1<ProductItem, Void>() {
			public Void call(ProductItem it) {
				System.out.println(it.getName());
				return null;
			}
		});

		System.out.println("-----");

		// 合計金額算出
		BigDecimal total = items.foldLeft(BigDecimal.ZERO,
			new Callable2<BigDecimal, ProductItem, BigDecimal>() {
				public BigDecimal call(BigDecimal a, ProductItem b) {
					return a.add(b.getPrice().multiply(new BigDecimal(b.getQty())));
				}
			}
		);

		System.out.println(total);
	}
}

