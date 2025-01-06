package com.vh_todo.todo.controller;

import com.vh_todo.todo.model.TodoItem;
import com.vh_todo.todo.service.TodoItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class TodoController {

    @Autowired
    private TodoItemService todoItemService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("todoItems", todoItemService.getAllTodoItems());
        model.addAttribute("today", LocalDate.now());
        return "index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("todoItem", new TodoItem());
        return "add-todo";
    }

    @PostMapping("/add")
    public String addTodoItem(@ModelAttribute TodoItem todoItem, RedirectAttributes redirectAttributes) {
        todoItemService.saveTodoItem(todoItem);
        redirectAttributes.addFlashAttribute("message", "Todo item added successfully!");
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        TodoItem todoItem = todoItemService.getTodoItemById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid todo item Id:" + id));
        model.addAttribute("todoItem", todoItem);
        return "edit-todo";
    }

    @PostMapping("/edit/{id}")
    public String updateTodoItem(@PathVariable Long id, @ModelAttribute TodoItem todoItem, RedirectAttributes redirectAttributes) {
        todoItemService.getTodoItemById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid todo item Id:" + id));
        todoItemService.saveTodoItem(todoItem);
        redirectAttributes.addFlashAttribute("message", "Todo item updated successfully!");
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteTodoItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        todoItemService.deleteTodoItem(id);
        redirectAttributes.addFlashAttribute("message", "Todo item deleted successfully!");
        return "redirect:/";
    }

    @PostMapping("/complete/{id}")
    public String completeTodoItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        TodoItem todoItem = todoItemService.getTodoItemById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid todo item Id:" + id));
        todoItem.setCompleted(true);
        todoItemService.saveTodoItem(todoItem);
        redirectAttributes.addFlashAttribute("message", "Todo item completed!");
        return "redirect:/";
    }
}