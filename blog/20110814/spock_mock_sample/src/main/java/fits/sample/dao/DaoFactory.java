package fits.sample.dao;

import fits.sample.dao.impl.TaskDaoImpl;

public class DaoFactory {
	private static DaoFactory instance = new DaoFactory();
	private TaskDao taskDao;

	private DaoFactory() {
		this.taskDao = new TaskDaoImpl();
	}

	public static DaoFactory getInstance() {
		return instance;
	}

	public TaskDao getTaskDao() {
		return this.taskDao;
	}

}

