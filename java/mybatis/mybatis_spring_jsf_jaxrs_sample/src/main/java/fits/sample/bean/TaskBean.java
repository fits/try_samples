package fits.sample.bean;

import java.util.List;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.inject.Inject;

import fits.sample.mapper.TaskMapper;
import fits.sample.model.TaskModel;

@ManagedBean
public class TaskBean {
	private String title;
	private Date date;
	private List<TaskModel> tasks;

	@Inject
	private TaskMapper taskMapper;

	public List<TaskModel> getTasks() {
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
		tasks = taskMapper.findByTitle(getTitle(), getDate());
	}
}
