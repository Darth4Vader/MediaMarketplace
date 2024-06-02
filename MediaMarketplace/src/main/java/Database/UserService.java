package Database;

import org.springframework.stereotype.Service;

import DataStructures.User;

@Service
public interface UserService {
	User getUserById(Long id);
}
