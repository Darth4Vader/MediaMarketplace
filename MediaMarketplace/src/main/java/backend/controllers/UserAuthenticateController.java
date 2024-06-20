package backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.dto.users.LogInDto;
import backend.dto.users.LogInResponseDto;
import backend.dto.users.UserInformationDto;
import backend.entities.User;
import backend.exceptions.LogValuesAreIncorrectException;
import backend.exceptions.UserAlreadyExistsException;
import backend.exceptions.UserDoesNotExistsException;
import backend.exceptions.UserNotLoggedInException;
import backend.exceptions.UserPasswordIsIncorrectException;
import backend.services.UserAuthenticateService;
import backend.services.UserServiceImpl;

@RestController
@RequestMapping("/auth")
public class UserAuthenticateController {

	@Autowired
	private UserAuthenticateService userAuthService;
	
    @PostMapping("/register")
    public String registerUser(@RequestBody UserInformationDto registerDto) throws UserAlreadyExistsException, LogValuesAreIncorrectException, UserPasswordIsIncorrectException {
        return userAuthService.registerUser(registerDto);
    }
    
    @PostMapping("/login")
    public LogInResponseDto loginUser(@RequestBody LogInDto loginDto) throws UserDoesNotExistsException, UserPasswordIsIncorrectException, LogValuesAreIncorrectException {
        return userAuthService.loginUser(loginDto);
    }
    
    @PostMapping("/update")
    public void updateUserInformation(UserInformationDto userDto) throws UserNotLoggedInException, UserPasswordIsIncorrectException, LogValuesAreIncorrectException {
    	userAuthService.updateUserInformation(userDto);
    }
    
    @PostMapping("/get/current_user")
    public UserInformationDto getCurrentUserDto() throws UserNotLoggedInException {
    	return userAuthService.getCurrentUserDto();
    }
    
    @PostMapping("/authenticate")
    public void authenticateLoggedUser() throws UserNotLoggedInException {
    	userAuthService.authenticateLoggedUser();
    }

}
