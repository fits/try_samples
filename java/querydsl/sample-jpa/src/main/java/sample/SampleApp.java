package sample;

import com.querydsl.jpa.impl.JPAQuery;
import lombok.val;
import sample.model.Product;
import sample.model.ProductVariation;
import sample.model.QProduct;

import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.util.List;

public class SampleApp {
	public static void main(String... args) throws Exception {
		val emf = Persistence.createEntityManagerFactory("jpa");
		val em = emf.createEntityManager();

		val tx = em.getTransaction();
		tx.begin();

		em.persist(product("sample1", "100", variation("White", "L"), variation("Black", "M")));
		em.persist(product("sample2", "200"));
		em.persist(product("abc3", "300"));

		tx.commit();

		val p = QProduct.product;
		val query = new JPAQuery<Product>(em);

		List<Product> res = query.from(p).where(p.name.startsWith("sample")).fetch();

		res.forEach(System.out::println);

		em.close();
	}

	private static Product product(String name, String price, ProductVariation... variations) {
		val res = new Product();

		res.setName(name);
		res.setPrice(new BigDecimal(price));

		for (val v : variations) {
			res.getVariationList().add(v);
			v.setProduct(res);
		}

		return res;
	}

	private static ProductVariation variation(String color, String size) {
		val res = new ProductVariation();

		res.setColor(color);
		res.setSize(size);

		return res;
	}
}
