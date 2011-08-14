package fits.sample.dao.impl;

import java.util.Map;
import java.util.HashMap;

import fits.sample.dao.TaskDao;
import fits.sample.model.Task;

public class TaskDaoImpl implements TaskDao {
	private Map<Integer, Task> map = new HashMap<Integer, Task>();

	public Task addTask(String title) {
		System.out.println("*** TaskDaoImpl.addTask()");

		Task t = new Task();
		t.setTitle(title);

		synchronized(map) {
			t.setTaskId(map.size() + 1);
			map.put(t.getTaskId(), t);
		}

		return t;
	}

	public Task getTask(Integer taskId) {
		return map.get(taskId);
	}
}

