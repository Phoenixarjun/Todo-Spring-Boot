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
        return (ArrayList<Todo>) db.query("SELECT id, todo, priority, status FROM TODOLIST", new TodoRowMapper());
    }

    @Override
    public Todo getTodoById(int id) {
        Todo todo = null;
        try {
            todo = db.queryForObject("SELECT id, todo, priority, status FROM TODOLIST WHERE id = ?",
                    new TodoRowMapper(), id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found with ID: " + id);
        }
        return todo;
    }

@Override
public Todo addTodo(Todo todo) {
    String sql = "INSERT INTO TODOLIST (todo, priority, status) VALUES (?, ?, ?)";
    db.update(sql, todo.getTodo(), todo.getPriority(), todo.getStatus());
    Integer id = db.queryForObject("SELECT MAX(id) FROM TODOLIST", Integer.class);
    return getTodoById(id);
}


    @Override
    public void deleteTodo(int id) {
        int rowsAffected = db.update("DELETE FROM TODOLIST WHERE id = ?", id);
        if (rowsAffected == 0) {
            throw new ResponseStatusException(HttpStatus.OK, "Todo not found with ID: " + id);
        }
    }

    @Override
    public Todo updateTodo(int id, Todo todo) {
        Todo existingTodo = getTodoById(id);

        if (todo.getTodo() != null) {
            db.update("UPDATE TODOLIST SET todo = ? WHERE id = ?", todo.getTodo(), id);
        }
        if (todo.getPriority() != null) {
            db.update("UPDATE TODOLIST SET priority = ? WHERE id = ?", todo.getPriority(), id);
        }
        if (todo.getStatus() != null) {
            db.update("UPDATE TODOLIST SET status = ? WHERE id = ?", todo.getStatus(), id);
        }
        return getTodoById(id);
    }
}
