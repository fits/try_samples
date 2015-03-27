package sample;

import com.mongodb.MongoClient;
import com.mysema.query.mongodb.morphia.MorphiaQuery;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import sample.model.Product;
import sample.model.QProduct;
import sample.model.Variation;

import java.net.UnknownHostException;

public class App {
	public static void main(String... args) throws UnknownHostException {
		Morphia morphia = new Morphia();
		Datastore ds = morphia.createDatastore(new MongoClient(), "sample");

		Product p = new Product("test" + System.currentTimeMillis());

		p.getVariationList().add(new Variation("L", "black"));
		p.getVariationList().add(new Variation("M", "white"));

		ds.save(p);

		Product tmpP = ds.get(p);
		tmpP.getVariationList().add(new Variation("S", "green"));

		ds.save(tmpP);

		QProduct qp = QProduct.product;
		MorphiaQuery<Product> query = new MorphiaQuery<>(morphia, ds, qp);

		query.where(qp.name.like("test%")).list().forEach(App::printProduct);
	}

	private static void printProduct(Product p) {
		System.out.println("----------");

		System.out.println(p.getId() + ", " + p.getName());

		p.getVariationList().forEach(v ->
				System.out.println(v.getColor() + ", " + v.getSize()));

	}
}