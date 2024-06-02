package Database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DataStructures.User;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	public UserServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public User getUserById(Long id) {
		return userRepository.findById(id).orElse(null);
	}

}
