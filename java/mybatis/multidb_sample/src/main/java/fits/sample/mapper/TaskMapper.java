package fits.sample.mapper;

import java.util.List;

import fits.sample.annotation.PrimaryDao;
import fits.sample.model.TaskModel;

@PrimaryDao
public interface TaskMapper {

	List<TaskModel> findAll();

	void insertTask(String title);

	int deleteTask(String taskId);
}
