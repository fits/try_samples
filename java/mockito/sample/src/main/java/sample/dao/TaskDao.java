package sample.dao;

import sample.model.Task;

public interface TaskDao {
	Task addTask(String title);
	Task getTask(String taskId);
}
