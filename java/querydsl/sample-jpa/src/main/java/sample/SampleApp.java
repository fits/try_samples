package sample;

import com.querydsl.jpa.impl.JPAQuery;
import lombok.val;
import sample.model.Product;
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

		em.persist(createProduct("sample1", "100"));
		em.persist(createProduct("sample2", "200"));
		em.persist(createProduct("abc3", "300"));

		tx.commit();

		QProduct p = QProduct.product;

		JPAQuery<Product> query = new JPAQuery<>(em);

		List<Product> res = query.from(p).where(p.name.startsWith("sample")).fetch();

		res.forEach(System.out::println);

		em.close();
	}

	private static Product createProduct(String name, String price) {
		val res = new Product();
		res.setName(name);
		res.setPrice(new BigDecimal(price));

		return res;
	}

}
