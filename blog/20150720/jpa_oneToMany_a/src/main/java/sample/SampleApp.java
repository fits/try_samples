package sample;

import lombok.val;

import sample.model.Product;
import sample.model.ProductVariation;

import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.util.List;

public class SampleApp {
	public static void main(String... args) throws Exception {
		val emf = Persistence.createEntityManagerFactory("jpa");
		val em = emf.createEntityManager();

		val tx = em.getTransaction();
		tx.begin();

		val p1 = product(
			"sample1", "50", 
			variation("White", "L"), 
			variation("Black", "M")
		);

		em.persist(p1);

		tx.commit();

		val cq = em.getCriteriaBuilder().createQuery(Product.class);

		List<Product> res = em.createQuery(cq).getResultList();

		res.forEach(System.out::println);

		em.close();
	}

	private static Product product(String name, String price, ProductVariation... variations) {
		val res = new Product();

		res.setName(name);
		res.setPrice(new BigDecimal(price));

		for (val v : variations) {
			res.getVariationList().add(v);
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
