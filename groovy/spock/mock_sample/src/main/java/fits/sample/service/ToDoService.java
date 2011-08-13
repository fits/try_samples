package fits.sample.service;

import fits.sample.model.Task;
import fits.sample.dao.TaskDao;
import fits.sample.dao.DaoFactory;

//サービスクラス
public class ToDoService {
	private TaskDao dao = DaoFactory.getInstance().getTaskDao();

	public String getTaskTitle(Integer taskId) {
		Task t = dao.getTask(taskId);
		return (t == null)? "": t.getTitle();
	}

	public void addTask(String title) {
		dao.addTask(title);
	}
}

