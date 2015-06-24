package sample.service;

import sample.dao.TaskDao;
import sample.model.Task;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

public class ToDoService {
	@Inject
	private TaskDao taskDao;

	public String getTaskTitle(String taskId) {
		Optional<Task> t = taskDao.getTask(taskId);

		return t.map(Task::getTitle).orElse("");
	}

	public void addTask(String title) {
		taskDao.setTask(new Task(generateId(), title));
	}

	public void updateTask(String taskId, String newTitle) {
		taskDao.getTask(taskId).ifPresent(t -> {
			taskDao.setTask(new Task(t.getTaskId(), newTitle));
		});
	}

	private String generateId() {
		return UUID.randomUUID().toString();
	}

}
