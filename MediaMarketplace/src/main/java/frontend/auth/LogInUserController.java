package frontend.auth;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.controllers.UserAuthenticateController;
import backend.dtos.users.LogInDto;
import backend.exceptions.LogValuesAreIncorrectException;
import backend.exceptions.UserDoesNotExistsException;
import backend.exceptions.UserPasswordIsIncorrectException;
import backend.exceptions.enums.UserLogInfo;
import frontend.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for handling user login functionality.
 * <p>This controller manages the user login interface, including user input validation,
 * password visibility toggling, and authentication actions.</p>
 */
@Component
public class LogInUserController {

    /** The FXML path for the login user page. */
    public static final String PATH = "/frontend/auth/LogInUser.fxml";
    
    /** TextField for user name input. */
    @FXML
    private TextField nameField;
    
    /** PasswordField for password input. */
    @FXML
    private PasswordField passwordField;
    
    /** TextField for displaying password (used for visibility toggle). */
    @FXML
    private TextField passwordTextField;
    
    /** Label for displaying error messages. */
    @FXML
    private Label errorLabel;
    
    /** CheckBox for toggling password visibility. */
    @FXML
    private CheckBox showPassword;
    
    /** Controller for user authentication. */
    @Autowired
    private UserAuthenticateController userAuth;
    
    /**
     * Initializes the controller and sets up listeners for input validation.
     * <p>Listens for changes in the user name and password fields to provide real-time feedback
     * and binds the password visibility toggle to switch between {@link PasswordField} and 
     * {@link TextField}.</p>
     */
    @FXML
    private void initialize() {
        nameField.textProperty().addListener(UserLogPageUtils.textFielsListener(nameField, errorLabel));
        passwordField.textProperty().addListener(UserLogPageUtils.textFielsListener(passwordTextField.getParent(), errorLabel));
        passwordField.textProperty().bindBidirectional(passwordTextField.textProperty());
    }
    
    /**
     * Switches to the user registration page.
     * <p>Activates the registration page, deactivating the current login page.</p>
     */
    @FXML
    private void changeToRegisterUserPage() {
        App.getApplicationInstance().activateLogPage(false);
    }
    
    /**
     * Toggles password visibility based on the CheckBox state.
     * <p>If the CheckBox is selected, displays the password in plain text. If not selected,
     * displays the password as masked text.</p>
     *
     * @param event The ActionEvent triggered by the CheckBox.
     */
    @FXML
    void passwordVisibility(ActionEvent event) {
        if (showPassword.isSelected()) {
            passwordTextField.toFront();
        } else {
            passwordField.toFront();
        }
    }
    
    /**
     * Handles the user login process.
     * <p>Attempts to log in the user with the provided name and password. In case of authentication
     * failure, displays appropriate error messages based on the type of exception.</p>
     */
    @FXML
    void createAccount() {
        String name = nameField.getText();
        String password = passwordField.getText();
        try {
            LogInDto dto = new LogInDto(name, password);
            this.userAuth.loginUser(dto);
            App.getApplicationInstance().closeLogPage();
        } catch (UserDoesNotExistsException e) {
            // This happens when the user account does not exist (username does not exist)
            // We will inform the user
            passwordField.setText("");
            errorLabel.setText("The user does not exist, use a different username.");
        } catch (UserPasswordIsIncorrectException e) {
            // The password does not match the password of the account that is associated with the username
            // We will inform the user
            passwordField.setText("");
            passwordField.getParent().setStyle("-fx-border-color: red");
            errorLabel.setText("The Password does not match the user password; try a different password.");
        } catch (LogValuesAreIncorrectException e) {
            // One of the input fields is not in a valid format
            // We will inform the user
            Set<UserLogInfo> userLogInfo = e.getUserLogInfo();
            if (userLogInfo != null) {
                if (userLogInfo.contains(UserLogInfo.NAME)) {
                    nameField.setStyle("-fx-border-color: red");
                }
                if (userLogInfo.contains(UserLogInfo.PASSWORD)) {
                    passwordField.getParent().setStyle("-fx-border-color: red");
                }
            }
            errorLabel.setText(e.getMessage());
        }
    }
}