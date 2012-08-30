package fits.sample.ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import fits.sample.model.TaskModel;

@Stateless
public class TaskEJB {
	@PersistenceContext
	private EntityManager em;

	@SuppressWarnings("unchecked")
	public List<TaskModel> findAll() {
		Query query = em.createQuery("select t from TaskModel t");
		return query.getResultList();
	}

}
