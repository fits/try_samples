package fits.sample.mapper;

import java.util.List;
import java.util.Date;

import org.apache.ibatis.annotations.Param;

import fits.sample.model.TasksModel;

public interface TasksMapper {

	List<TasksModel> findByTitleAndCreated(
		@Param("title") String title, @Param("created") Date created
	);
}
