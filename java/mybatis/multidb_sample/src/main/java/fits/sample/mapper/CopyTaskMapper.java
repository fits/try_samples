package fits.sample.mapper;

import fits.sample.annotation.SecondaryDao;
import fits.sample.model.TaskModel;

@SecondaryDao
public interface CopyTaskMapper {

	void insertTask(TaskModel task);

	int deleteAllTasks();
}
