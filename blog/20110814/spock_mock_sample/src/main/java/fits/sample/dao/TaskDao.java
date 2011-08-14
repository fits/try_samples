package fits.sample.dao;

import fits.sample.model.Task;

public interface TaskDao {

	Task addTask(String title);

	Task getTask(Integer taskId);
}

