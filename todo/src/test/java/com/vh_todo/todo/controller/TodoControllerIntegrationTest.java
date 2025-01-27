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
 * Integration tests for the TodoController class.
 * These tests validate the entire flow from controller to service to repository and back, using an in-memory H2 database.
 * The application-test.properties profile is activated for these tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Activates the "test" profile for an isolated test environment
class TodoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc; // MockMvc for simulating HTTP requests and responses.

    @Autowired
    private TodoItemRepository todoItemRepository; // Repository for interacting with the database.

    /**
     * Sets up the test environment before each test case.
     * Clears the database and seeds it with a sample TodoItem to ensure consistent results.
     */
    @BeforeEach
    void setUp() {
        // Clear the database to avoid interference from previous tests.
        todoItemRepository.deleteAll();

        // Seed the database with a sample TodoItem for testing purposes.
        TodoItem sample = new TodoItem();
        sample.setDescription("Sample Todo");
        sample.setDueDate(LocalDate.now().plusDays(7));
        sample.setCompleted(false);
        todoItemRepository.save(sample);
    }

    /**
     * Tests the retrieval of all todo items.
     * Verifies that the response contains at least one item (seeded in @BeforeEach)
     * and that the item matches the expected properties.
     */
    @Test
    void testGetAllTodos() throws Exception {
        mockMvc.perform(get("/api/todos")) // Perform GET request to /api/todos
                .andExpect(status().isOk()) // Expect HTTP 200 status
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1)))) // Verify at least 1 item in response
                .andExpect(jsonPath("$[0].description").value("Sample Todo")); // Verify the description of the first item
    }

    /**
     * Tests the creation of a new todo item.
     * Sends a POST request with JSON data and verifies that the response contains the expected properties.
     */
    @Test
    void testCreateTodo() throws Exception {
        String jsonBody = """
            {
              "description": "Buy milk",
              "completed": false
            }
            """; // JSON payload representing the new TodoItem

        mockMvc.perform(post("/api/todos/add") // Perform POST request to /api/todos/add
                        .contentType(MediaType.APPLICATION_JSON) // Set Content-Type header to JSON
                        .content(jsonBody)) // Send the JSON payload
                .andExpect(status().isOk()) // Expect HTTP 200 status
                .andExpect(jsonPath("$.description").value("Buy milk")) // Verify the description of the created item
                .andExpect(jsonPath("$.id").exists()); // Verify that the ID is present in the response
    }

    /**
     * Tests retrieving a todo item by its ID.
     * Fetches an existing item from the database and verifies that the response matches its properties.
     */
    @Test
    void testGetTodoById() throws Exception {
        // Retrieve the first item from the seeded database
        TodoItem existing = todoItemRepository.findAll().get(0);

        mockMvc.perform(get("/api/todos/" + existing.getId())) // Perform GET request to /api/todos/{id}
                .andExpect(status().isOk()) // Expect HTTP 200 status
                .andExpect(jsonPath("$.description").value("Sample Todo")); // Verify the description matches the item
    }

    /**
     * Tests updating an existing todo item.
     * Sends a PUT request with updated properties and verifies that the response reflects the changes.
     */
    @Test
    void testUpdateTodo() throws Exception {
        // Retrieve the first item from the seeded database
        TodoItem existing = todoItemRepository.findAll().get(0);

        // JSON payload with updated properties for the existing item
        String jsonBody = """
            {
              "id": %d,
              "description": "Updated Todo",
              "completed": true
            }
            """.formatted(existing.getId());

        mockMvc.perform(put("/api/todos/" + existing.getId()) // Perform PUT request to /api/todos/{id}
                        .contentType(MediaType.APPLICATION_JSON) // Set Content-Type header to JSON
                        .content(jsonBody)) // Send the JSON payload
                .andExpect(status().isOk()) // Expect HTTP 200 status
                .andExpect(jsonPath("$.description").value("Updated Todo")) // Verify the updated description
                .andExpect(jsonPath("$.completed").value(true)); // Verify the updated completion status
    }
}