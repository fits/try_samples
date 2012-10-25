
import java.math.BigDecimal;

import fj.Effect;
import fj.F;
import fj.F2;
import fj.data.List;
import static fj.data.List.*;

public class CalcSample {
	public static void main(String[] args) {

		List<ProductItem> items = list(
			new ProductItem("id1", "商品1", new BigDecimal("1000"), 1),
			new ProductItem("id2", "商品2", new BigDecimal("3000"), 2),
			new ProductItem("id3", "商品3", new BigDecimal("1500"), 3)
		);

		List<String> names = items.map(new F<ProductItem, String>() {
			public String f(ProductItem it) {
				return it.getName();
			}
		});

		names.foreach(new Effect<String>() {
			public void e(String n) {
				System.out.println(n);
			}
		});

		System.out.println("-----");

		List<ProductItem> highItems = items.filter(new F<ProductItem, Boolean>() {
			public Boolean f(ProductItem it) {
				return it.getPrice().compareTo(new BigDecimal("1500")) >= 0;
			}
		});

		highItems.foreach(new Effect<ProductItem>() {
			public void e(ProductItem it) {
				System.out.println(it.getName());
			}
		});

		System.out.println("-----");

		// 合計金額算出
		BigDecimal total = items.foldLeft(
			new F2<BigDecimal, ProductItem, BigDecimal>() {
				public BigDecimal f(BigDecimal a, ProductItem b) {
					return a.add(b.getPrice().multiply(new BigDecimal(b.getQty())));
				}
			},
			BigDecimal.ZERO
		);

		System.out.println(total);
	}
}

