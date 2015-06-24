package sample.dao.impl;

import sample.dao.TaskDao;
import sample.model.Task;

import javax.inject.Named;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Named
public class TaskDaoImpl implements TaskDao {
	private Map<String, Task> map = new ConcurrentHashMap<>();

	@Override
	public Task addTask(String title) {
		Task task = new Task(generateId(), title);

		map.put(task.getTaskId(), task);

		return task;
	}

	@Override
	public Task getTask(String taskId) {
		return map.get(taskId);
	}

	private String generateId() {
		return UUID.randomUUID().toString();
	}
}
