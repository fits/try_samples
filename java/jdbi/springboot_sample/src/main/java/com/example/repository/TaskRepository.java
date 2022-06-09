package com.example.repository;

import com.example.data.Task;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class TaskRepository {
    @Autowired
    private Jdbi jdbi;

    public Optional<Task> findById(long id) {
        return jdbi.withHandle(h -> selectById(h, id));
    }

    public List<Task> findByStatus(Task.Status status) {
        return jdbi.withHandle(h -> {
            return h.createQuery("SELECT * FROM tasks WHERE status = :status")
                    .bind("status", status)
                    .map(this::toData)
                    .list();
        });
    }

    public Task create(String subject) {
        return jdbi.withHandle(h -> {
            h.createUpdate("INSERT INTO tasks (subject, status) VALUES (:subject, :status)")
                    .bind("subject", subject)
                    .bind("status", Task.Status.ready)
                    .execute();

            return h.createQuery("SELECT * FROM tasks WHERE id = LAST_INSERT_ID()")
                    .map(this::toData)
                    .first();
        });
    }

    public Optional<Task> updateStatus(long id, Task.Status status) {
        return jdbi.withHandle(h -> {
            var res = h.createUpdate("UPDATE tasks SET status = :status WHERE id = :id")
                    .bind("status", status)
                    .bind("id", id)
                    .execute();

            if (res == 0) {
                return Optional.empty();
            }

            return selectById(h, id);
        });
    }

    private Optional<Task> selectById(Handle h, long id) {
        return h.createQuery("SELECT * FROM tasks WHERE id = :id")
                .bind("id", id)
                .map(this::toData)
                .findOne();
    }

    private Task toData(ResultSet rs, StatementContext _ctx) throws SQLException {
        return new Task(
                rs.getLong("id"),
                rs.getString("subject"),
                Task.Status.valueOf(rs.getString("status"))
        );
    }
}
