package sample.service;

import sample.dao.TaskDao;
import sample.model.Task;

import javax.inject.Inject;

public class ToDoService {
	@Inject
	private TaskDao taskDao;

	public String getTaskTitle(String taskId) {
		Task t = taskDao.getTask(taskId);
		return (t == null)? "": t.getTitle();
	}

	public void addTask(String title) {
		taskDao.addTask(title);
	}
}
