package fits.sample.mapper;

import java.util.List;
import java.util.Date;

import org.apache.ibatis.annotations.Param;

import fits.sample.model.TaskModel;

public interface TaskMapper {

	List<TaskModel> findAll();

	List<TaskModel> findByTitle(
		@Param("title") String title, @Param("created") Date created
	);
}
