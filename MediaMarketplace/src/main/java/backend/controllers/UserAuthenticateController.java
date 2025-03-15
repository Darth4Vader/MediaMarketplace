package backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.dtos.users.LogInDto;
import backend.dtos.users.UserInformationDto;
import backend.exceptions.EntityAdditionException;
import backend.exceptions.LogValuesAreIncorrectException;
import backend.exceptions.UserAlreadyExistsException;
import backend.exceptions.UserDoesNotExistsException;
import backend.exceptions.UserNotLoggedInException;
import backend.exceptions.UserPasswordIsIncorrectException;
import backend.services.UserAuthenticateService;

/**
 * REST controller for user authentication and management.
 * <p>
 * This controller provides endpoints for user registration, login, information updates, and authentication checks.
 * </p>
 */
@RestController
@RequestMapping("/auth")
public class UserAuthenticateController {

    @Autowired
    private UserAuthenticateService userAuthService;
    
    /**
     * Registers a new user.
     * <p>
     * This endpoint registers a new user with the provided {@link UserInformationDto}.
     * If the user already exists or if the provided values are incorrect, appropriate exceptions will be thrown.
     * </p>
     *
     * @param registerDto The {@link UserInformationDto} object containing the details of the user to be registered.
     * @return A success message indicating the user has been registered.
     * @throws UserAlreadyExistsException If a user with the same details already exists.
     * @throws LogValuesAreIncorrectException If the provided values are incorrect.
     * @throws UserPasswordIsIncorrectException If the password provided is incorrect.
     */
    @PostMapping("/register")
    public String registerUser(@RequestBody UserInformationDto registerDto) throws UserAlreadyExistsException, LogValuesAreIncorrectException, UserPasswordIsIncorrectException {
        try {
            return userAuthService.registerUser(registerDto);
        } catch (DataAccessException e) {
            throw new EntityAdditionException("Unable to register the user", e);
        }
    }
    
    /**
     * Logs in a user.
     * <p>
     * This endpoint logs in a user using the provided {@link LogInDto}.
     * If the user does not exist or the password is incorrect, appropriate exceptions will be thrown.
     * </p>
     *
     * @param loginDto The {@link LogInDto} object containing the login credentials of the user.
     * @return A success message indicating the user has been logged in.
     * @throws UserDoesNotExistsException If the user does not exist.
     * @throws UserPasswordIsIncorrectException If the provided password is incorrect.
     * @throws LogValuesAreIncorrectException If the provided login values are incorrect.
     */
    @PostMapping("/login")
    public String loginUser(@RequestBody LogInDto loginDto) throws UserDoesNotExistsException, UserPasswordIsIncorrectException, LogValuesAreIncorrectException {
        return userAuthService.loginUser(loginDto);
    }
    
    /**
     * Updates the information of the currently logged-in user.
     * <p>
     * This endpoint updates the user information using the provided {@link UserInformationDto}.
     * If the user is not logged in or if the password is incorrect, appropriate exceptions will be thrown.
     * </p>
     *
     * @param userDto The {@link UserInformationDto} object containing the updated user information.
     * @throws UserNotLoggedInException If the user is not logged in.
     * @throws UserPasswordIsIncorrectException If the provided password is incorrect.
     * @throws LogValuesAreIncorrectException If the provided values are incorrect.
     */
    @PostMapping("/update")
    public void updateUserInformation(@RequestBody UserInformationDto userDto) throws UserNotLoggedInException, UserPasswordIsIncorrectException, LogValuesAreIncorrectException {
        try {
            userAuthService.updateUserInformation(userDto);
        } catch (DataAccessException e) {
            throw new EntityAdditionException("Unable to update the user information", e);
        }
    }
    
    /**
     * Signs out the currently logged-in user.
     * <p>
     * This endpoint triggers the user sign-out process by calling the {@link UserAuthService#signOutFromCurrentUser()} method.
     * </p>
     * @throws UserNotLoggedInException if no user is currently logged in
     */
    @GetMapping("/sign_out")
    public void signOutFromCurrentUser() throws UserNotLoggedInException {
    	userAuthService.signOutFromCurrentUser();
    }
    
    /**
     * Checks if the current user is an administrator.
     * <p>
     * This endpoint checks the current user's role to determine if they are an administrator.
     * </p>
     */
    @GetMapping("/checkIfAdmin")
    public void checkIfCurrentUserIsAdmin() {
        userAuthService.checkIfCurrentUserIsAdmin();
    }
    
    /**
     * Retrieves the information of the currently logged-in user.
     * <p>
     * This endpoint returns the {@link UserInformationDto} for the currently logged-in user.
     * If the user is not logged in, an exception will be thrown.
     * </p>
     *
     * @return The {@link UserInformationDto} object containing the current user's information.
     * @throws UserNotLoggedInException If the user is not logged in.
     */
    @GetMapping("/get/current_user")
    public UserInformationDto getCurrentUserDto() throws UserNotLoggedInException {
        return userAuthService.getCurrentUserDto();
    }
    
    /**
     * Checks if the current user is an administrator.
     * <p>
     * This endpoint returns a boolean value indicating whether the current user is an administrator.
     * If the user is not logged in, an exception will be thrown.
     * </p>
     *
     * @return {@code true} if the current user is an administrator, {@code false} otherwise.
     * @throws UserNotLoggedInException If the user is not logged in.
     */
    @GetMapping("/is_admin/current_user")
    public boolean isCurrentUserAdmin() throws UserNotLoggedInException {
        return userAuthService.isCurrentUserAdmin();
    }
    
    /**
     * Authenticates the currently logged-in user.
     * <p>
     * This endpoint performs an authentication check on the current user.
     * If the user is not logged in, an exception will be thrown.
     * </p>
     *
     * @throws UserNotLoggedInException If the user is not logged in.
     */
    @GetMapping("/authenticate")
    public void authenticateLoggedUser() throws UserNotLoggedInException {
        userAuthService.authenticateLoggedUser();
    }
}