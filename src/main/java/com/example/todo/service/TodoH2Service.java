package com.example.todo.service;

import com.example.todo.model.Todo;
import com.example.todo.model.TodoRowMapper;
import com.example.todo.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;

@Service
public class TodoH2Service implements TodoRepository {

    @Autowired
    private JdbcTemplate db;

    @Override
    public ArrayList<Todo> getTodos() {
        return (ArrayList<Todo>) db.query("SELECT * FROM TODOLIST", new TodoRowMapper());
    }

    @Override
    public Todo getTodoById(int id) {
        try {
            return db.queryForObject("SELECT * FROM TODOLIST WHERE id = ?", new TodoRowMapper(), id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found with ID: " + id);
        }
    }

    @Override
    public Todo addTodo(Todo todo) {
        String sql = "INSERT INTO TODOLIST (todo, status, priority) VALUES (?, ?, ?)";
        db.update(sql, todo.getTodo(), todo.getStatus(), todo.getPriority());
        return db.queryForObject("SELECT * FROM TODOLIST WHERE id = ?", new TodoRowMapper(), todo.getId());
    }

    @Override
    public void deleteTodo(int id) {
        db.update("DELETE FROM TODOLIST WHERE id = ?", id);
    }

    @Override
    public Todo updateTodo(int id, Todo todo) {
        Todo existingTodo = getTodoById(id);

        if (todo.getTodo() != null) {
            db.update("UPDATE TODOLIST SET todo = ? WHERE id = ?", todo.getTodo(), id);
        }
        if (todo.getStatus() != null) {
            db.update("UPDATE TODOLIST SET status = ? WHERE id = ?", todo.getStatus(), id);
        }
        if (todo.getPriority() != null) {
            db.update("UPDATE TODOLIST SET priority = ? WHERE id = ?", todo.getPriority(), id);
        }
        return db.queryForObject("SELECT * FROM TODOLIST WHERE id = ?", new TodoRowMapper(), id);
    }
}
