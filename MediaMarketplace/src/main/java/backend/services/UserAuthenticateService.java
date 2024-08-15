package backend.services;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.DataUtils;
import backend.auth.AuthenticateAdmin;
import backend.dto.users.LogInDto;
import backend.dto.users.LogInResponseDto;
import backend.dto.users.UserInformationDto;
import backend.entities.Role;
import backend.entities.RoleType;
import backend.entities.User;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.LogValuesAreIncorrectException;
import backend.exceptions.UserAlreadyExistsException;
import backend.exceptions.UserDoesNotExistsException;
import backend.exceptions.UserNotLoggedInException;
import backend.exceptions.UserPasswordIsIncorrectException;
import backend.repositories.RoleRepository;
import backend.repositories.UserRepository;

/**
 * Service for user authentication and registration.
 * <p>
 * This service handles user registration, login, and updates. It uses Spring Security's {@link AuthenticationManager}
 * to manage authentication and a {@link PasswordEncoder} for password encryption. It also manages user roles and
 * provides methods to check if the current user has admin privileges.
 * </p>
 */
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

    /**
     * Registers a new user with the provided information.
     * <p>
     * This method validates the registration information, checks if the username already exists, encodes the user's password,
     * assigns default roles, and saves the new user. After registration, it logs the user in automatically.
     * </p>
     * 
     * @param registerDto The DTO containing user registration information.
     * @return A success message if the registration is successful.
     * @throws UserAlreadyExistsException If a user with the same username already exists.
     * @throws LogValuesAreIncorrectException If the provided login values are incorrect.
     * @throws UserPasswordIsIncorrectException If the password does not meet validation criteria.
     */
    @Transactional
    public String registerUser(UserInformationDto registerDto) throws UserAlreadyExistsException, LogValuesAreIncorrectException, UserPasswordIsIncorrectException {
    	//check that the userName does not exists
    	validateUserDto(registerDto);
    	String username = registerDto.getUsername();
    	if(userRepository.findByUsername(username).isPresent())
    		throw new UserAlreadyExistsException();
    	//encode the password
    	String password = registerDto.getPassword();
    	String encodedPassword = encodePassword(password);
        //create the authorities for the new user
    	Role userRole = roleRepository.findByRoleType(RoleType.ROLE_USER).get();
        Set<Role> authorities = new HashSet<>();
        authorities.add(userRole);
        User newUser = new User(username, encodedPassword, authorities);
        userRepository.save(newUser);
        //login user after registering finished
        try {
			loginUser(username, password);
		} catch (UserDoesNotExistsException | UserPasswordIsIncorrectException | LogValuesAreIncorrectException e) {
			//we just created so no exception can be made
			//but in case of a glitch, we will throw a runtime exception
			throw new RuntimeException(e);
		}
        return "succsses";
    }
    
    /**
     * Logs in a user using the provided login information.
     * <p>
     * This method authenticates the user and generates a JWT token if the credentials are valid.
     * </p>
     * 
     * @param loginDto The DTO containing login information.
     * @return A {@link LogInResponseDto} containing the generated JWT token.
     * @throws UserDoesNotExistsException If the user does not exist.
     * @throws UserPasswordIsIncorrectException If the password is incorrect.
     * @throws LogValuesAreIncorrectException If the provided login values are incorrect.
     */
    public LogInResponseDto loginUser(LogInDto loginDto) throws UserDoesNotExistsException, UserPasswordIsIncorrectException, LogValuesAreIncorrectException {
    	String username = loginDto.getUserName();
    	String password = loginDto.getPassword();
    	return loginUser(username, password);
    }
    
    /**
     * Logs in a user with the specified username and password.
     * <p>
     * This method performs authentication and generates a JWT token if the credentials are valid.
     * </p>
     * 
     * @param username The username of the user.
     * @param password The password of the user.
     * @return A {@link LogInResponseDto} containing the generated JWT token.
     * @throws UserDoesNotExistsException If the user does not exist.
     * @throws UserPasswordIsIncorrectException If the password is incorrect.
     * @throws LogValuesAreIncorrectException If the provided login values are incorrect.
     */
    private LogInResponseDto loginUser(String username, String password) throws UserDoesNotExistsException, UserPasswordIsIncorrectException, LogValuesAreIncorrectException {
    	LogValuesAreIncorrectException.checkForException(username, password);
    	Optional<User> userOPt = userRepository.findByUsername(username);
    	//if we can' find the user by his username, then he doesn't exists.
    	if(userOPt.isEmpty())
    		throw new UserDoesNotExistsException();
    	try {
	        Authentication auth = authenticationManager.authenticate(
	            new UsernamePasswordAuthenticationToken(username, password, userOPt.get().getAuthorities()));
	        //set as the current authentication user
	        SecurityContextHolder.getContext().setAuthentication(auth);
	        //generate the Jwt token
	        String token = tokenService.generateJwt(auth);
	        return new LogInResponseDto(token);
    } catch(AuthenticationException e) {
    	//if there is a problem with authenticating the user, then the password is incorrect, because we check that the username exists, so it can only be the password.
        SecurityContextHolder.getContext().setAuthentication(null);
        throw new UserPasswordIsIncorrectException();
    	}
    }
    
    /**
     * Updates the current user's information.
     * <p>
     * This method updates user details if the current user is logged in and has the correct password.
     * </p>
     * 
     * @param userDto The DTO containing user information to be updated.
     * @throws UserNotLoggedInException If the user is not logged in.
     * @throws UserPasswordIsIncorrectException If the provided password is incorrect.
     * @throws LogValuesAreIncorrectException If the provided values are incorrect.
     */
    @Transactional
    public void updateUserInformation(UserInformationDto userDto) throws UserNotLoggedInException, UserPasswordIsIncorrectException, LogValuesAreIncorrectException {
    	//check that the current user is trying to chenge is information, and not a different user.
    	User authUser = tokenService.getCurretUser();
    	String username = authUser.getUsername();
    	if(!Objects.equals(username, userDto.getUsername()))
    		throw new UserNotLoggedInException("The user:" + userDto.getUsername() + " is not logged, cannot update information");
    	String password = userDto.getPassword();
    	String passwordConfirm = userDto.getPasswordConfirm();
    	//we load the user from the database.
    	User user = userRepository.findByUsername(username).get();
    	if(DataUtils.isNotBlank(password) || DataUtils.isNotBlank(passwordConfirm)) {
    		//if there is a new password, then  check that it is the same as the confirm password
    		//otherwise throw an exception for the user to know the problem
    		LogValuesAreIncorrectException.checkForException(userDto);
	    	if(!Objects.equals(password, userDto.getPasswordConfirm()))
	    		throw new UserPasswordIsIncorrectException("Password not matching");
	    	//encode the passwor, and set it for the user
	    	user.setPassword(encodePassword(userDto.getPassword()));
    	}
    	user.setName(userDto.getName());
    	//after the change we will reloging the user again to a new session with his updated information.
    	reloadAuthentication(user);
    }
    
    /**
     * Verifies if the user is currently logged in by checking the authentication token.
     * 
     * @throws UserNotLoggedInException If no user is logged in.
     */
    public void authenticateLoggedUser() throws UserNotLoggedInException {
    	tokenService.getCurretUser();
    }
    
    /**
     * Checks if the current user is an admin.
     * <p>
     * This method uses the {@link AuthenticateAdmin} annotation to check the user's admin status.
     * </p>
     */
    @AuthenticateAdmin
    public void checkIfCurrentUserIsAdmin() {
    	
    }
    
    /**
     * Determines if the current user is an admin.
     * <p>
     * This method checks the user's roles to determine if they have admin privileges.
     * </p>
     * 
     * @return true if the current user has admin privileges; false otherwise.
     */
    public boolean isCurrentUserAdmin() {
    	User user = tokenService.getCurretUser();
		try {
			Role admin = roleRepository.findByRoleType(RoleType.ROLE_ADMIN).orElseThrow(() -> new EntityNotFoundException("The role ADMIN does not exists"));
			Collection<? extends GrantedAuthority> roles = user.getAuthorities();
			return roles != null && roles.contains(admin);
		} catch (EntityNotFoundException e) {
			//we only check, therefore if the role does not exists the n we will return false.
		}
		return false;
    }
    
    /**
     * Retrieves the UserInformationDto (like username, name) for the currently logged-in user.
     * <p>
     * This method converts the currently authenticated {@link User} to a {@link UserInformationDto}.
     * </p>
     * 
     * @return The {@link UserInformationDto} for the current user.
     * @throws UserNotLoggedInException If no user is logged in.
     */
    public UserInformationDto getCurrentUserDto() throws UserNotLoggedInException {
    	return convertUserToDto(tokenService.getCurretUser());
    }
    
    /**
     * Reloads the authentication for the given user.
     * <p>
     * This method creates a new {@link Authentication} object for the specified user and updates the
     * {@link SecurityContextHolder} with the new authentication. It is typically used after updating user
     * information to refresh the security context.
     * </p>
     * 
     * @param user The {@link User} object representing the user to be authenticated.
     */
    private void reloadAuthentication(User user) {
        Authentication auth = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    
    /**
     * Validates the user information DTO.
     * <p>
     * This method checks for inconsistencies in the user data transfer object (DTO), such as password mismatches
     * and invalid values. It throws exceptions if the DTO is invalid.
     * </p>
     * 
     * @param userDto The {@link UserInformationDto} object containing user information to be validated.
     * @throws UserPasswordIsIncorrectException If the passwords do not match.
     * @throws LogValuesAreIncorrectException If any of the provided values are incorrect.
     */
    private void validateUserDto(UserInformationDto userDto) throws UserPasswordIsIncorrectException, LogValuesAreIncorrectException {
    	LogValuesAreIncorrectException.checkForException(userDto);
    	String password = userDto.getPassword();
    	if(!Objects.equals(password, userDto.getPasswordConfirm()))
    		throw new UserPasswordIsIncorrectException("Password not matching");
    }
    
    /**
     * Encodes the provided password using a password encoder.
     * <p>
     * This method applies encoding to the given plain text password to ensure it is stored securely. The encoded password
     * can then be used for authentication purposes.
     * </p>
     * 
     * @param password The plain text password to be encoded.
     * @return The encoded password as a {@link String}.
     */
    private String encodePassword(String password) {
    	return passwordEncoder.encode(password);
    }
    
    /**
     * Converts a {@link User} entity to a {@link UserInformationDto}.
     * 
     * @param user The {@link User} entity to convert.
     * @return The corresponding {@link UserInformationDto}.
     */
    public UserInformationDto convertUserToDto(User user) {
    	UserInformationDto userDto = new UserInformationDto();
    	userDto.setUsername(user.getUsername());
    	userDto.setName(user.getName());
    	return userDto;
    }
}