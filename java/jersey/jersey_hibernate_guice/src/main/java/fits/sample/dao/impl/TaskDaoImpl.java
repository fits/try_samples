package fits.sample.dao.impl;

import java.math.BigInteger;
import java.sql.Timestamp;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.google.inject.persist.Transactional;

import fits.sample.dao.TaskDao;
import fits.sample.model.Task;

public class TaskDaoImpl implements TaskDao {
	@Inject
	private EntityManager em;

	@Transactional
	public Task addTask(String title) {
		Task t = this.createTask(title);

		em.persist(t);

		return t;
	}

	public Task getTask(BigInteger taskId) {
		return em.find(Task.class, taskId);
	}

	private Task createTask(String title) {
		Task t = new Task();
		t.setTitle(title);
		t.setCreated(new Timestamp(System.currentTimeMillis()));
		t.setModified(new Timestamp(System.currentTimeMillis()));

		return t;
	}
}

