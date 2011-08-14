package fits.sample.service;

import fits.sample.model.Task;
import fits.sample.dao.TaskDao;
import fits.sample.dao.DaoFactory;

//サービスクラス
public class ToDoService {
	private TaskDao dao = DaoFactory.getInstance().getTaskDao();

	public String getTaskTitle(Integer taskId) throws NoTaskException {
		Task t = dao.getTask(taskId);

		if (t == null) {
			throw new NoTaskException();
		}

		return t.getTitle();
	}

	public boolean addTask(String title) {
		boolean result = false;

		try {
			dao.addTask(title);
			result = true;
		} catch (Exception ex) {
		}

		return result;
	}
}

