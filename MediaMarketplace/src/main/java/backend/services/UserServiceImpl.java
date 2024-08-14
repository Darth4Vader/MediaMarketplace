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

	@Autowired
	private UserRepository userRepository;
	
    public User getUserByUsername(String username) throws UsernameNotFoundException {
    	return userRepository
        		.findByUsername(username)
        		.orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
	
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	return getUserByUsername(username);
    }
}