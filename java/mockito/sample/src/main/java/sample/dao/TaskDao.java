package sample.dao;

import sample.model.Task;

import javax.inject.Named;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Named
public class TaskDao {
	private Map<String, Task> map = new ConcurrentHashMap<>();

	public void setTask(Task task) {
		map.put(task.getTaskId(), task);
	}

	public Optional<Task> getTask(String taskId) {
		return Optional.ofNullable(map.get(taskId));
	}
}
