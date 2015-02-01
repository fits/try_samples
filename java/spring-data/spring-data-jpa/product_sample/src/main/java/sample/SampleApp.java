package sample;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import sample.dao.ProductRepository;
import sample.model.Product;

import java.math.BigDecimal;

public class SampleApp {
	public static void main(String... args) {
		AnnotationConfigApplicationContext ctx =
				new AnnotationConfigApplicationContext("sample");

		ProductRepository dao = ctx.getBean(ProductRepository.class);

		dao.findAll().forEach(SampleApp::printProduct);
		System.out.println("-----");

		dao.findByPriceLessThanEqual(new BigDecimal("500")).forEach(SampleApp::printProduct);
		System.out.println("-----");

		dao.findByPriceLessThanEqual(new BigDecimal("1000")).forEach(SampleApp::printProduct);
	}

	private static void printProduct(Product p) {
		System.out.printf("id: %s, name: %s, price: %s, date: %s \n",
				p.getId(), p.getName(), p.getPrice(), p.getReleaseDate());
	}
}
