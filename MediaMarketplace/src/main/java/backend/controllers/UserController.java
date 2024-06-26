package backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.entities.User;
import backend.services.UserServiceImpl;

@RestController
@RequestMapping("/users")
public class UserController {
	
	
	private UserServiceImpl userService; 
	
	public UserController() {
	}

	@Autowired
	public UserController(UserServiceImpl userService) {
		this.userService = userService;
	}
	
	@GetMapping("/{id}")
	public User getUserById(@PathVariable Long id) {
		return null;//userService.getUserById(id);
	}

}
