package com.vh_todo.todo.service;

import com.vh_todo.todo.model.TodoItem;
import com.vh_todo.todo.repository.TodoItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoItemServiceTest {

    @Mock
    private TodoItemRepository todoItemRepository;

    @InjectMocks
    private TodoItemService todoItemService;

    @Test
    void testGetAllTodoItems() {
        // Given
        TodoItem item1 = new TodoItem();
        item1.setDescription("Todo 1");
        TodoItem item2 = new TodoItem();
        item2.setDescription("Todo 2");
        when(todoItemRepository.findAll()).thenReturn(Arrays.asList(item1, item2));

        // When
        List<TodoItem> results = todoItemService.getAllTodoItems();

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getDescription()).isEqualTo("Todo 1");
        assertThat(results.get(1).getDescription()).isEqualTo("Todo 2");
    }

    @Test
    void testGetTodoItemById_Found() {
        // Given
        Long id = 123L;
        TodoItem item = new TodoItem();
        item.setId(id);
        item.setDescription("Todo 123");
        when(todoItemRepository.findById(id)).thenReturn(Optional.of(item));

        // When
        Optional<TodoItem> result = todoItemService.getTodoItemById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getDescription()).isEqualTo("Todo 123");
        verify(todoItemRepository, times(1)).findById(id);
    }

    @Test
    void testGetTodoItemById_NotFound() {
        // Given
        Long id = 999L;
        when(todoItemRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<TodoItem> result = todoItemService.getTodoItemById(id);

        // Then
        assertThat(result).isNotPresent();
        verify(todoItemRepository, times(1)).findById(id);
    }

    @Test
    void testSaveTodoItem() {
        // Given
        TodoItem itemToSave = new TodoItem();
        itemToSave.setDescription("New Todo");
        itemToSave.setDueDate(LocalDate.of(2025, 2, 14));

        TodoItem savedItem = new TodoItem();
        savedItem.setId(1L);
        savedItem.setDescription("New Todo");
        savedItem.setDueDate(LocalDate.of(2025, 2, 14));

        when(todoItemRepository.save(itemToSave)).thenReturn(savedItem);

        // When
        TodoItem result = todoItemService.saveTodoItem(itemToSave);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("New Todo");
        assertThat(result.getDueDate()).isEqualTo(LocalDate.of(2025, 2, 14));
        verify(todoItemRepository, times(1)).save(itemToSave);
    }

    @Test
    void testDeleteTodoItem() {
        // Given
        Long id = 100L;

        // When
        todoItemService.deleteTodoItem(id);

        // Then
        verify(todoItemRepository, times(1)).deleteById(id);
    }
}
