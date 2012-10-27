
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Predicate;
import static org.apache.commons.collections.TransformerUtils.*;
import static org.apache.commons.collections.CollectionUtils.*;

public class CommonsCollectionsSample {
	public static void main(String[] args) {

		List<ProductItem> items = Arrays.asList(
			new ProductItem("id1", "商品1", new BigDecimal("1000"), 1),
			new ProductItem("id2", "商品2", new BigDecimal("3000"), 2),
			new ProductItem("id3", "商品3", new BigDecimal("1500"), 3)
		);
		// (1) マッピング
		Collection names = collect(items, invokerTransformer("getName"));

		forAllDo(names, new Closure() {
			public void execute(Object n) {
				System.out.println(n);
			}
		});

		System.out.println("-----");

		// (2) フィルタリング
		Collection highItems = select(items, new Predicate() {
			public boolean evaluate(Object obj) {
				ProductItem it = (ProductItem)obj;
				return it.getPrice().compareTo(new BigDecimal("1500")) >= 0;
			}
		});

		forAllDo(highItems, new Closure() {
			public void execute(Object obj) {
				ProductItem it = (ProductItem)obj;
				System.out.println(it.getName());
			}
		});

		System.out.println("-----");

		// (3) 畳み込み
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

