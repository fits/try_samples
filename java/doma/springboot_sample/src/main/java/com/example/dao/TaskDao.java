package com.example.dao;

import com.example.data.Task;
import org.seasar.doma.*;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.Result;

import java.util.List;
import java.util.Optional;

@Dao
@ConfigAutowireable
public interface TaskDao {
    @Sql("select * from tasks where id = /* id */99")
    @Select
    Optional<Task> selectById(long id);

    @Sql("select * from tasks where status = /* status */'ready'")
    @Select
    List<Task> selectByStatus(Task.Status status);

    @Insert
    Result<Task> insert(Task task);

    @Update(exclude = "subject")
    Result<Task> update(Task task);
}
