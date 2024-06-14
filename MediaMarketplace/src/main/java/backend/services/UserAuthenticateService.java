package backend.services;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.dto.users.LogInDto;
import backend.dto.users.LogInResponseDto;
import backend.dto.users.RegisterDto;
import backend.entities.Role;
import backend.entities.User;
import backend.exceptions.LogValuesAreIncorrectException;
import backend.exceptions.UserAlreadyExistsException;
import backend.exceptions.UserDoesNotExistsException;
import backend.exceptions.UserPasswordIsIncorrectException;
import backend.repositories.RoleRepository;
import backend.repositories.UserRepository;

@Service
public class UserAuthenticateService {
	
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    public String registerUser(RegisterDto registerDto) throws UserAlreadyExistsException, LogValuesAreIncorrectException {
    	//check that the userName does not exists
    	String username = registerDto.getUserName();
    	String password = registerDto.getPassword();
    	LogValuesAreIncorrectException.checkForException(username, password);
    	if(userRepository.findByUserName(username).isPresent())
    		throw new UserAlreadyExistsException();
    	//encode the password
    	String encodedPassword = passwordEncoder.encode(password);
        //create the authorities for the new user
    	Role userRole = roleRepository.findByAuthority("USER").get();
        Set<Role> authorities = new HashSet<>();
        authorities.add(userRole);
        User newUser = new User(username, encodedPassword, authorities);
        userRepository.save(newUser);
        return "succsses";
    }

    public LogInResponseDto loginUser(LogInDto loginDto) throws UserDoesNotExistsException, UserPasswordIsIncorrectException, LogValuesAreIncorrectException {
    	String username = loginDto.getUserName();
    	String password = loginDto.getPassword();
    	LogValuesAreIncorrectException.checkForException(username, password);
    	if(userRepository.findByUserName(username).isEmpty())
    		throw new UserDoesNotExistsException();
    	try {
	        Authentication auth = authenticationManager.authenticate(
	            new UsernamePasswordAuthenticationToken(username, password));
	        SecurityContextHolder.getContext().setAuthentication(auth);
	        String token = tokenService.generateJwt(auth);
	        System.out.println("Token: " + token);
	        return new LogInResponseDto(userRepository.findByUserName(username).get(), token);
    } catch(AuthenticationException e) {
        SecurityContextHolder.getContext().setAuthentication(null);
        throw new UserPasswordIsIncorrectException();
    }
    }
	
}
