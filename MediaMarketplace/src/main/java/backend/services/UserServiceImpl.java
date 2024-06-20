package backend.services;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import backend.dto.users.UserInformationDto;
import backend.entities.User;
import backend.exceptions.UserNotLoggedInException;
import backend.exceptions.UserPasswordIsIncorrectException;
import backend.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
    
    @Autowired
    private TokenService tokenService;
	
    public User getUserByUsername(String username) throws UsernameNotFoundException {
    	return userRepository
        		.findByUsername(username)
        		.orElseThrow(() -> new UsernameNotFoundException("User not found"));
    	/*User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;*/
    }
	
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	return getUserByUsername(username);
    	/*User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;*/
    }
}