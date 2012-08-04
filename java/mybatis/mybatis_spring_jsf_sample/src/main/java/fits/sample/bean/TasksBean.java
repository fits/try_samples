package fits.sample.bean;

import java.util.List;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.inject.Inject;

import fits.sample.mapper.TasksMapper;
import fits.sample.model.TasksModel;

@ManagedBean
public class TasksBean {
	private String title;
	private Date date;
	private List<TasksModel> tasks;

	@Inject
	private TasksMapper tasksMapper;

	public List<TasksModel> getTasks() {
		return tasks;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void search() {
		tasks = tasksMapper.findByTitleAndCreated(getTitle(), getDate());
	}
}
