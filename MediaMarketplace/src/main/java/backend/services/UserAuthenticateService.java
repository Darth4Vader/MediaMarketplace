package backend.services;

import java.util.HashSet;
import java.util.Objects;
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

import backend.DataUtils;
import backend.dto.users.LogInDto;
import backend.dto.users.LogInResponseDto;
import backend.dto.users.UserInformationDto;
import backend.entities.Role;
import backend.entities.User;
import backend.exceptions.LogValuesAreIncorrectException;
import backend.exceptions.UserAlreadyExistsException;
import backend.exceptions.UserDoesNotExistsException;
import backend.exceptions.UserNotLoggedInException;
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

    public String registerUser(UserInformationDto registerDto) throws UserAlreadyExistsException, LogValuesAreIncorrectException, UserPasswordIsIncorrectException {
    	//check that the userName does not exists
    	validateUserDto(registerDto);
    	String username = registerDto.getUsername();
    	if(userRepository.findByUsername(username).isPresent())
    		throw new UserAlreadyExistsException();
    	//encode the password
    	String encodedPassword = encodePassword(registerDto.getPassword());
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
    	if(userRepository.findByUsername(username).isEmpty())
    		throw new UserDoesNotExistsException();
    	try {
	        Authentication auth = authenticationManager.authenticate(
	            new UsernamePasswordAuthenticationToken(username, password));
	        SecurityContextHolder.getContext().setAuthentication(auth);
	        String token = tokenService.generateJwt(auth);
	        System.out.println("Token: " + token);
	        return new LogInResponseDto(userRepository.findByUsername(username).get(), token);
    } catch(AuthenticationException e) {
        SecurityContextHolder.getContext().setAuthentication(null);
        throw new UserPasswordIsIncorrectException();
    	}
    }
    
    @Transactional
    public void updateUserInformation(UserInformationDto userDto) throws UserNotLoggedInException, UserPasswordIsIncorrectException, LogValuesAreIncorrectException {
    	User authUser = tokenService.getCurretUser();
    	String username = authUser.getUsername();
    	if(!Objects.equals(username, userDto.getUsername()))
    		throw new UserNotLoggedInException("The user:" + userDto.getUsername() + " is not logged, cannot update information");
    	String password = userDto.getPassword();
    	String passwordConfirm = userDto.getPasswordConfirm();
    	User user = userRepository.findByUsername(username).get();
    	if(DataUtils.isNotBlank(password) || DataUtils.isNotBlank(passwordConfirm)) {
    		LogValuesAreIncorrectException.checkForException(userDto);
	    	if(!Objects.equals(password, userDto.getPasswordConfirm()))
	    		throw new UserPasswordIsIncorrectException("Password not matching");
	    	user.setPassword(encodePassword(userDto.getPassword()));
    	}
    	user.setName(userDto.getName());
    	reloadAuthentication(user);
        
        //remove all this green
    	//validateAuthentication(user);
    	//userRepository.save(user);
    	/*User g = userRepository.findByUserName(username).get();
    	System.out.println(g.equals(curUser));
    	System.out.println("Side d " + userDto.getName());
    	System.out.println("Cur User: " + curUser.getUsername() + " Password: " + curUser.getPassword() + " Name: " + curUser.getName());
    	System.out.println("User: " + user.getUsername() + " Password: " + user.getPassword() + " Name: " + user.getName());
    	System.out.println("Updated User: " + g.getUsername() + " Password: " + g.getPassword() + " Name: " + g.getName());
    	
    	/*User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;*/
    }
    
    public UserInformationDto getCurrentUserDto() throws UserNotLoggedInException {
    	return convertUserToDto(tokenService.getCurretUser());
    }
    
    private void reloadAuthentication(User user) {
        Authentication auth = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    
    private void validateUserDto(UserInformationDto userDto) throws UserPasswordIsIncorrectException, LogValuesAreIncorrectException {
    	LogValuesAreIncorrectException.checkForException(userDto);
    	String password = userDto.getPassword();
    	if(!Objects.equals(password, userDto.getPasswordConfirm()))
    		throw new UserPasswordIsIncorrectException("Password not matching");
    }
    
    private String encodePassword(String password) {
    	return passwordEncoder.encode(password);
    }
    
    public UserInformationDto convertUserToDto(User user) {
    	UserInformationDto userDto = new UserInformationDto();
    	userDto.setUsername(user.getUsername());
    	userDto.setName(user.getName());
    	return userDto;
    }
    
    public void authenticateLoggedUser() throws UserNotLoggedInException {
    	tokenService.getCurretUser();
    }
	
}
