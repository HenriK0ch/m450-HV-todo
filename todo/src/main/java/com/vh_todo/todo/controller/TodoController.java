package com.vh_todo.todo.controller;

import com.vh_todo.todo.model.TodoItem;
import com.vh_todo.todo.service.TodoItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    @Autowired
    private TodoItemService todoItemService;

    @GetMapping
    public List<TodoItem> getAllTodos() {
        return todoItemService.getAllTodoItems();
    }

    @GetMapping("/{id}")
    public TodoItem getTodoById(@PathVariable Long id) {
        return todoItemService.getTodoItemById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Todo ID: " + id));
    }

    @PostMapping("/add")
    public TodoItem createTodo(@RequestBody TodoItem todoItem) {
        return todoItemService.saveTodoItem(todoItem);
    }

    @PutMapping("/{id}")
    public TodoItem updateTodo(@PathVariable Long id, @RequestBody TodoItem updatedItem) {
        todoItemService.getTodoItemById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Todo ID: " + id));
        return todoItemService.saveTodoItem(updatedItem);
    }

    @PutMapping("/{id}/extend")
    public TodoItem extendDate(@PathVariable Long id, @RequestBody int days) {
        return todoItemService.extendDueDate(id, days);
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable Long id) {
        todoItemService.deleteTodoItem(id);
    }
}
