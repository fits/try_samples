package fits.sample;

import java.util.ArrayList;

import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hom.EntityManagerImpl;

public class Main {
	public static void main(String[] args) {

		Cluster cluster = HFactory.getOrCreateCluster("Test Cluster", "localhost:9160");
		Keyspace keyspace = HFactory.createKeyspace("Sample", cluster);

		EntityManagerImpl em = new EntityManagerImpl(keyspace, "fits.sample");

		Order data = new Order();
		data.setId("jid1");
		data.setUserId("U1");
		data.setLines(new ArrayList<OrderLine>());

		OrderLine line1 = new OrderLine();
		line1.productId = "P1";
		line1.quantity = 1;
		data.getLines().add(line1);

		OrderLine line2 = new OrderLine();
		line2.productId = "P2";
		line2.quantity = 2;
		data.getLines().add(line2);

		em.persist(data);

		Order res = em.find(Order.class, "jid1");
		System.out.printf("%s - %s\n", res.getId(), res.getUserId());

		for (OrderLine line : res.getLines()) {
			System.out.printf("%s, %d\n", line.productId, line.quantity);
		}
	}

}
