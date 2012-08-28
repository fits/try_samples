package fits.sample.resource;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import fits.sample.mapper.TaskMapper;
import fits.sample.model.TaskModel;

@Path("/rest/tasks")
public class TaskResource {
	@Inject
	private TaskMapper taskMapper;

	@GET
	@Produces("application/json")
	public List<TaskModel> getTasks() {
		return taskMapper.findAll();
	}
}
