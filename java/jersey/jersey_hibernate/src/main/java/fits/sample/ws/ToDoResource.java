package fits.sample.ws;

import java.math.BigInteger;

import javax.persistence.EntityManager;
import javax.ws.rs.*;

import fits.sample.model.Task;

@Path("/")
public class ToDoResource {
	private EntityManager em = DaoHelper.getInstance().getEntityManager();

	@GET
	@Path("{taskId}")
	@Produces("text/plain")
	public Task getTask(@PathParam("taskId") BigInteger taskId) {
		return em.find(Task.class, taskId);
	}
}

