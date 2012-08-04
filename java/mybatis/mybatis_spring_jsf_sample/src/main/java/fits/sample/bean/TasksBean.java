package fits.sample.bean;

import java.util.List;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.inject.Inject;

import fits.sample.mapper.TasksMapper;
import fits.sample.model.TasksModel;

@ManagedBean
public class TasksBean {
	@Inject
	private TasksMapper tasksMapper;

	public List<TasksModel> getTasks() {
		return tasksMapper.findByTitleAndCreated("test%", new Date(10000));
	}
}
