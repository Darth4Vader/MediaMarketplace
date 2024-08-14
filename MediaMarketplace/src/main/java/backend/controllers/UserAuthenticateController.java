package backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.dto.users.LogInDto;
import backend.dto.users.LogInResponseDto;
import backend.dto.users.UserInformationDto;
import backend.exceptions.EntityAdditionException;
import backend.exceptions.LogValuesAreIncorrectException;
import backend.exceptions.UserAlreadyExistsException;
import backend.exceptions.UserDoesNotExistsException;
import backend.exceptions.UserNotLoggedInException;
import backend.exceptions.UserPasswordIsIncorrectException;
import backend.services.UserAuthenticateService;

@RestController
@RequestMapping("/auth")
public class UserAuthenticateController {

	@Autowired
	private UserAuthenticateService userAuthService;
	
    @PostMapping("/register")
    public String registerUser(@RequestBody UserInformationDto registerDto) throws UserAlreadyExistsException, LogValuesAreIncorrectException, UserPasswordIsIncorrectException {
        try {
        	return userAuthService.registerUser(registerDto);
        }
		catch (DataAccessException e) {//we will catch the Transactional runtime exception, and throw it as a custom exception
			throw new EntityAdditionException("Unable to register the user", e);
		}
    }
    
    @PostMapping("/login")
    public LogInResponseDto loginUser(@RequestBody LogInDto loginDto) throws UserDoesNotExistsException, UserPasswordIsIncorrectException, LogValuesAreIncorrectException {
        return userAuthService.loginUser(loginDto);
    }
    
    @PostMapping("/update")
    public void updateUserInformation(UserInformationDto userDto) throws UserNotLoggedInException, UserPasswordIsIncorrectException, LogValuesAreIncorrectException {
    	try {
    		userAuthService.updateUserInformation(userDto);
    	}
		catch (DataAccessException e) {//we will catch the Transactional runtime exception, and throw it as a custom exception
			throw new EntityAdditionException("Unable to update the user information", e);
		}
    }
    
    @PostMapping("/checkIfAdmin")
    public void checkIfCurrentUserIsAdmin() {
    	userAuthService.checkIfCurrentUserIsAdmin();
    }
    
    @PostMapping("/get/current_user")
    public UserInformationDto getCurrentUserDto() throws UserNotLoggedInException {
    	return userAuthService.getCurrentUserDto();
    }
    
    @PostMapping("/is_admin/current_user")
    public boolean isCurrentUserAdmin() throws UserNotLoggedInException {
    	return userAuthService.isCurrentUserAdmin();
    }
    
    @PostMapping("/authenticate")
    public void authenticateLoggedUser() throws UserNotLoggedInException {
    	userAuthService.authenticateLoggedUser();
    }

}
