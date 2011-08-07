package fits.sample.ws;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DaoHelper {
	private static DaoHelper instance = new DaoHelper();
	private EntityManager em;

	private DaoHelper() {
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("todo");
		this.em = factory.createEntityManager();
	}

	public EntityManager getEntityManager() {
		return this.em;
	}

	public static DaoHelper getInstance() {
		return instance;
	}
}
