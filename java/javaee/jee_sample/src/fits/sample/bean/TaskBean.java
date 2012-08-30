package fits.sample.bean;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.inject.Inject;

import fits.sample.ejb.TaskEJB;
import fits.sample.model.TaskModel;

@ManagedBean
public class TaskBean {
	@Inject
	private TaskEJB taskEjb;

	public List<TaskModel> getTasks() {
		return taskEjb.findAll();
	}
}
