import static org.junit.jupiter.api.Assertions.*;

//import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
//import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;

import backend.ActivateSpringApplication;
import backend.controllers.UserController;
import backend.entities.User;

@SpringBootTest(classes = ActivateSpringApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringJUnitConfig(ApplicationTestConfig.class)
class UserControllerTest {

	@Autowired
	private UserController userController;

	@Test
	public void contextLoads() {
	}

	/*@Test
	public void testGetAllUsers() {
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

		ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + "/users",
				HttpMethod.GET, entity, String.class);

		assertNotNull(response.getBody());
	}*/

	@Test
	public void testGetUserById() {
		User user = userController.getUserById(2l);
		assertNull(user);
		User user2 = userController.getUserById(1l);
		assertNotNull(user2);
	}
	
	
	
	

	/*@Test
	public void testCreateUser() {
		User user = new User();
		user.setEmail("admin@gmail.com");
		user.setFirstName("admin");
		user.setLastName("admin");
		user.setCreatedBy("admin");
		user.setUpdatedBy("admin");

		ResponseEntity<User> postResponse = restTemplate.postForEntity(getRootUrl() + "/users", user, User.class);
		Assert.assertNotNull(postResponse);
		Assert.assertNotNull(postResponse.getBody());
	}*/

	/*@Test
	public void testUpdatePost() {
		int id = 1;
		User user = restTemplate.getForObject(getRootUrl() + "/users/" + id, User.class);
		user.setFirstName("admin1");
		user.setLastName("admin2");

		restTemplate.put(getRootUrl() + "/users/" + id, user);

		User updatedUser = restTemplate.getForObject(getRootUrl() + "/users/" + id, User.class);
		Assert.assertNotNull(updatedUser);
	}*/

	/*@Test
	public void testDeletePost() {
		int id = 2;
		User user = restTemplate.getForObject(getRootUrl() + "/users/" + id, User.class);
		Assert.assertNotNull(user);

		restTemplate.delete(getRootUrl() + "/users/" + id);

		try {
			user = restTemplate.getForObject(getRootUrl() + "/users/" + id, User.class);
		} catch (final HttpClientErrorException e) {
			Assert.assertEquals(e.getStatusCode(), HttpStatus.NOT_FOUND);
		}
	}*/

}