package backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import backend.entities.User;
import backend.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;
    
	@Autowired
	public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
	
    public User getUserByUserName(String username) throws UsernameNotFoundException {
    	return userRepository
        		.findByUserName(username)
        		.orElseThrow(() -> new UsernameNotFoundException("User not found"));
    	/*User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;*/
    }
	
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	return getUserByUserName(username);
    	/*User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;*/
    }
}