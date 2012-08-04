package fits.sample.bean;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.inject.Inject;

import fits.sample.mapper.TasksMapper;
import fits.sample.model.TasksModel;

@ManagedBean
public class TasksBean {
	private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	private String title;
	private String date;
	private List<TasksModel> tasks = new ArrayList<>();

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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void search() {
		try {
			Date created = df.parse(getDate());
			tasks = tasksMapper.findByTitleAndCreated(getTitle(), created);
		} catch (ParseException e) {
			e.printStackTrace();
			tasks = new ArrayList<>();
		}
	}
}
