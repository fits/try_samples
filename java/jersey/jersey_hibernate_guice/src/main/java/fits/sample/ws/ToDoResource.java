package fits.sample.ws;

import java.math.BigInteger;

import javax.inject.Inject;
import javax.ws.rs.*;

import fits.sample.model.Task;
import fits.sample.dao.TaskDao;

@Path("/")
public class ToDoResource {
	@Inject
	private TaskDao dao;

	@GET
	@Path("task/{taskId}")
	@Produces("text/plain")
	public Task getTask(@PathParam("taskId") BigInteger taskId) {
		return dao.getTask(taskId);
	}

	@POST
	@Path("task")
	public Task addTask(@FormParam("title") String title) throws Exception {
		return dao.addTask(title);
	}
}

