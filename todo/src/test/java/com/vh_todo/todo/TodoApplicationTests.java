package com.vh_todo.todo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("ci") // Use the CI profile for testing
class TodoApplicationTests {

	@Test
	void contextLoads() {
		// Test logic here
	}
}
