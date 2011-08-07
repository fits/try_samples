package fits.sample.dao;

import java.math.BigInteger;
import fits.sample.model.Task;

public interface TaskDao {

	Task addTask(String title);

	Task getTask(BigInteger id);
}

