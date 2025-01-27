package com.vh_todo.todo.service;

import com.vh_todo.todo.model.TodoItem;
import com.vh_todo.todo.repository.TodoItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TodoItemService {

    @Autowired
    private TodoItemRepository todoItemRepository;

    public List<TodoItem> getAllTodoItems() {
        return todoItemRepository.findAll();
    }

    public Optional<TodoItem> getTodoItemById(Long id) {
        return todoItemRepository.findById(id);
    }

    public TodoItem saveTodoItem(TodoItem todoItem) {
        return todoItemRepository.save(todoItem);
    }

    public void deleteTodoItem(Long id) {
        todoItemRepository.deleteById(id);
    }

    public TodoItem extendDueDate(Long id, int days) {
        TodoItem todoItem = getTodoItemById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Todo ID: " + id));
        todoItem.setDueDate(todoItem.getDueDate().plusDays(days));
        return saveTodoItem(todoItem);
    }

    public TodoItem shortenDueDate(Long id, int days) {
        TodoItem todoItem = getTodoItemById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Todo ID: " + id));
        todoItem.setDueDate(todoItem.getDueDate().minusDays(days));
        return saveTodoItem(todoItem);
    }
}