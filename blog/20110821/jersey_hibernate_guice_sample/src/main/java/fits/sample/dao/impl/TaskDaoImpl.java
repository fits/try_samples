package fits.sample.dao.impl;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;

import com.google.inject.persist.Transactional;

import fits.sample.dao.TaskDao;
import fits.sample.model.Task;

public class TaskDaoImpl implements TaskDao {
	@Inject
	private EntityManager em;

	@Transactional
	public long addTask(String title) {
		Task t = this.createTask(title);

		em.persist(t);

		return t.getTaskId();
	}

	public List<Task> getTaskList() {
		CriteriaQuery<Task> q = em.getCriteriaBuilder().createQuery(Task.class);
		return em.createQuery(q.select(q.from(Task.class))).getResultList();
	}

	private Task createTask(String title) {
		Task t = new Task();
		t.setTitle(title);
		t.setCreatedDate(new Date());

		return t;
	}
}

