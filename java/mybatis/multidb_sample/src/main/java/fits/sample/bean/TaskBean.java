package fits.sample.bean;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.inject.Inject;

import fits.sample.mapper.TaskMapper;
import fits.sample.model.TaskModel;
import fits.sample.service.CopyTaskService;

@ManagedBean
public class TaskBean {
	private String title;
	private int count;

	@Inject
	private TaskMapper taskMapper;
	@Inject
	private CopyTaskService copyTaskService;

	public TaskBean() {
		System.out.println("*** init TaskBean");
	}

	public List<TaskModel> getTasks() {
		return taskMapper.findAll();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void insert() {
		taskMapper.insertTask(getTitle());
		setCount(1);
	}

	public void copy() {
		int res = copyTaskService.copy();
		setCount(res);
	}
}
