package fits.sample.ws;

import java.math.BigInteger;
import java.sql.Timestamp;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.ws.rs.*;

import fits.sample.model.Task;

@Path("/")
public class ToDoResource {
	private EntityManager em = DaoHelper.getInstance().getEntityManager();

	@GET
	@Path("task/{taskId}")
	@Produces("text/plain")
	public Task getTask(@PathParam("taskId") BigInteger taskId) {
		return em.find(Task.class, taskId);
	}

	@POST
	@Path("task")
	public Task addTask(@FormParam("title") String title) throws Exception {
		System.out.println(title);

		EntityTransaction tr = em.getTransaction();
		tr.begin();

		Task t = createTask(title);
		try {
			em.persist(t);
			tr.commit();
		}
		catch(Exception ex) {
			tr.rollback();
			throw ex;
		}
		return t;
	}

	private Task createTask(String title) {
		Task t = new Task();
		t.setTitle(title);
		t.setCreated(new Timestamp(System.currentTimeMillis()));
		t.setModified(new Timestamp(System.currentTimeMillis()));

		return t;
	}
}

