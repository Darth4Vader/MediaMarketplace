package backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backend.entities.User;
import backend.repositories.UserRepository;

//@Service
public class UserServiceImpl2 implements UserService2 {
	
	@Autowired
	private UserRepository userRepository;
	
	public UserServiceImpl2() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public User getUserById(Long id) {
		return userRepository.findById(id).orElse(null);
	}

	@Override
	public void saveUser(User user) {
		// TODO Auto-generated method stub
		
	}

}
