package com.vh_todo.todo.controller;

import com.vh_todo.todo.model.TodoItem;
import com.vh_todo.todo.repository.TodoItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Full integration test (Controller -> Service -> Repository -> DB).
 * Uses H2 if 'application-test.properties' is set.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Activates application-test.properties if present
class TodoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoItemRepository todoItemRepository;

    @BeforeEach
    void setUp() {
        // Clear & seed DB before each test (optional)
        todoItemRepository.deleteAll();
        TodoItem sample = new TodoItem();
        sample.setDescription("Sample Todo");
        sample.setDueDate(LocalDate.now().plusDays(7));
        sample.setCompleted(false);
        todoItemRepository.save(sample);
    }

    @Test
    void testGetAllTodos() throws Exception {
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                // We expect at least 1 item from our @BeforeEach
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].description").value("Sample Todo"));
    }

    @Test
    void testCreateTodo() throws Exception {
        String jsonBody = """
            {
              "description": "Buy milk",
              "completed": false
            }
            """;

        mockMvc.perform(post("/api/todos/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                // Inspect the response content
                .andExpect(jsonPath("$.description").value("Buy milk"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testGetTodoById() throws Exception {
        // Grab existing item from DB
        TodoItem existing = todoItemRepository.findAll().get(0);

        mockMvc.perform(get("/api/todos/" + existing.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Sample Todo"));
    }

    @Test
    void testUpdateTodo() throws Exception {
        TodoItem existing = todoItemRepository.findAll().get(0);
        String jsonBody = """
            {
              "id": %d,
              "description": "Updated Todo",
              "completed": true
            }
            """.formatted(existing.getId());

        mockMvc.perform(put("/api/todos/" + existing.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated Todo"))
                .andExpect(jsonPath("$.completed").value(true));
    }
}
