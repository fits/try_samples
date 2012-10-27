
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.streams.Stream;

public class Java8Sample {
	public static void main(String[] args) {

		List<ProductItem> items = Arrays.asList(
			new ProductItem("id1", "商品1", new BigDecimal("1000"), 1),
			new ProductItem("id2", "商品2", new BigDecimal("3000"), 2),
			new ProductItem("id3", "商品3", new BigDecimal("1500"), 3)
		);
		// (1) マッピング
		Stream<String> names = items.stream().map(it -> it.getName());

		names.forEach(n -> System.out.println(n));

		System.out.println("-----");

		// (2) フィルタリング
		Stream<ProductItem> highItems = items.stream().filter(it -> it.getPrice().compareTo(new BigDecimal("1500")) >= 0);

		highItems.forEach(it -> System.out.println(it.getName()));

		System.out.println("-----");

		// (3) 畳み込み
		BigDecimal total = items.stream().fold(
			() -> BigDecimal.ZERO, 
			(a, b) -> a.add(b.getPrice().multiply(new BigDecimal(b.getQty()))), 
			null
		);

		System.out.println(total);
	}
}

