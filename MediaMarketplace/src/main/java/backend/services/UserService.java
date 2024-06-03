package backend.services;

import org.springframework.stereotype.Service;

import backend.entities.User;

@Service
public interface UserService {
	User getUserById(Long id);
	void saveUser(User user);
	
}
