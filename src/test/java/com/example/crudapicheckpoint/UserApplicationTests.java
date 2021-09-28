package com.example.crudapicheckpoint;

import com.example.crudapicheckpoint.Model.User;
import com.example.crudapicheckpoint.Repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class UserApplicationTests {

	@Autowired
	MockMvc mvc;

	@Autowired
	UserRepository newUserRepository;

	@Transactional
	@Rollback
	@Test
	void testGetRequestReturnsAllUsers() throws Exception {
		//Setup
		User user1 = new User();
		user1.setEmail("test@test.com");
		this.newUserRepository.save(user1);

		//Execute
		this.mvc.perform(get("/users"))

		//Assert
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].email", is("test@test.com")));
	}

	@Transactional
	@Rollback
	@Test
	void testPostRequestAddsNewUser() throws Exception {
		//Setup
		String jsonString = "{\"email\":\"john@example.com\",\"password\":\"something-secret\"}";
		//Execute
		this.mvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonString))

				//Assert
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email", is("john@example.com")));
	}

	@Transactional
	@Rollback
	@Test
	void testGetUserByIdReturnsSingleUser() throws Exception {
		//Setup
		User user1 = new User();
		User user2 = new User();
		User user3 = new User();
		user1.setEmail("test1@test.com");
		user2.setEmail("test2@test.com");
		user3.setEmail("test3@test.com");

		this.newUserRepository.save(user1);
		this.newUserRepository.save(user2);
		this.newUserRepository.save(user3);
		//Execute
		this.mvc.perform(get(String.format("/users/%d", user2.getId())))

				//Assert
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email", is("test2@test.com")));
	}

	@Transactional
	@Rollback
	@Test
	void testPatchModifiesExistingUser() throws Exception {
		//Setup
		String jsonString = "{\"email\":\"john@example.com\",\"password\":\"something-secret\"}";
		User user1 = new User();
		User user2 = new User();
		User user3 = new User();
		user1.setEmail("test1@test.com");
		user2.setEmail("test2@test.com");
		user3.setEmail("test3@test.com");

		this.newUserRepository.save(user1);
		this.newUserRepository.save(user2);
		this.newUserRepository.save(user3);
		//Execute
		this.mvc.perform(patch(String.format("/users/%d", user2.getId()))
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonString))

				//Assert
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email", is("john@example.com")));
	}

	@Transactional
	@Rollback
	@Test
	void testDeleteRemovesUserFromDatabaseAndReturnsCount() throws Exception {
		//Setup
		String jsonString = "{\"authenticated\":false,\"email\":\"john@example.com\",\"password\":\"something-secret\"}";
		User user1 = new User();
		User user2 = new User();
		User user3 = new User();
		user1.setEmail("test1@test.com");
		user1.setAuthenticated(true);
		user2.setEmail("test2@test.com");
		user2.setAuthenticated(true);
		user3.setEmail("test3@test.com");
		user3.setAuthenticated(true);

		this.newUserRepository.save(user1);
		this.newUserRepository.save(user2);
		this.newUserRepository.save(user3);
		//Execute
		this.mvc.perform(delete(String.format("/users/%d", user2.getId()))
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonString))

				//Assert
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.count", is(2)));
	}

	@Transactional
	@Rollback
	@Test
	void testPostUsersAuthenticatedChecksIfTrueReturnsUserInfo() throws Exception {
		//Setup
		String jsonInput = "{\"email\":\"test2@test.com\",\"password\":\"1234\"}";
		User user1 = new User();
		User user2 = new User();
		User user3 = new User();
		user1.setEmail("test1@test.com");
		user1.setPassword("4321");
		user2.setEmail("test2@test.com");
		user2.setPassword("1234");
		user3.setEmail("test3@test.com");
		user3.setPassword("1234");

		this.newUserRepository.save(user1);
		this.newUserRepository.save(user2);
		this.newUserRepository.save(user3);
		String jsonOutput = String.format("{\"authenticated\":true,\"user\":{\"id\":%d,\"email\":\"test2@test.com\"}}", user2.getId());
		//Execute
		this.mvc.perform(post("/users/authenticate")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonInput))

				//Assert
				.andExpect(status().isOk())
				.andExpect(content().json(jsonOutput));
	}

	@Transactional
	@Rollback
	@Test
	void testPostUsersAuthenticatedChecksIfFalseReturnsUserInfo() throws Exception {
		//Setup
		String jsonInput = "{\"email\":\"test2@test.com\",\"password\":\"134\"}";
		User user1 = new User();
		User user2 = new User();
		User user3 = new User();
		user1.setEmail("test1@test.com");
		user1.setPassword("4321");
		user2.setEmail("test2@test.com");
		user2.setPassword("1234");
		user3.setEmail("test3@test.com");
		user3.setPassword("1234");

		this.newUserRepository.save(user1);
		this.newUserRepository.save(user2);
		this.newUserRepository.save(user3);
		String jsonOutput = "{\"authenticated\":false}";
		//Execute
		this.mvc.perform(post("/users/authenticate")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonInput))

				//Assert
				.andExpect(status().isOk())
				.andExpect(content().json(jsonOutput));
	}
}
