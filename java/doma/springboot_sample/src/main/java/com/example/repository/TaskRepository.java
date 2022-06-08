package com.example.repository;

import com.example.dao.TaskDao;
import com.example.data.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

@Repository
public class TaskRepository {
    @Autowired
    private TaskDao dao;

    public Optional<Task> findById(long id) {
        return dao.selectById(id);
    }

    public List<Task> findByStatus(Task.Status status) {
        return dao.selectByStatus(status);
    }

    public Task create(String subject) {
        var task = new Task(OptionalLong.empty(), subject, Task.Status.ready);
        return dao.insert(task).getEntity();
    }

    public Optional<Task> updateStatus(long id, Task.Status status) {
        return findById(id).map(t ->
            dao.update(new Task(t.id(), t.subject(), status)).getEntity()
        );
    }
}
