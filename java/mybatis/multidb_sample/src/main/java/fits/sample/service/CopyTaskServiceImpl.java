package fits.sample.service;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import fits.sample.mapper.CopyTaskMapper;
import fits.sample.mapper.TaskMapper;
import fits.sample.model.TaskModel;

@Named("copyTaskService")
@Transactional("transactionManager2")
public class CopyTaskServiceImpl implements CopyTaskService {
	@Inject
	private TaskMapper taskMapper;
	@Inject
	private CopyTaskMapper copyTaskMapper;

	@Override
	public int copy() {
		copyTaskMapper.deleteAllTasks();

		int result = 0;
		for (TaskModel task : taskMapper.findAll()) {
			copyTaskMapper.insertTask(task);
			result++;
		}
		return result;
	}
}
