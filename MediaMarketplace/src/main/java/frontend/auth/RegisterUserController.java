package frontend.auth;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.controllers.UserAuthenticateController;
import backend.dtos.users.UserInformationDto;
import backend.exceptions.LogValuesAreIncorrectException;
import backend.exceptions.UserAlreadyExistsException;
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
 * Controller for handling user registration functionality.
 * <p>This controller manages the user registration interface, including input validation,
 * password visibility toggling, and account creation actions.</p>
 */
@Component
public class RegisterUserController {

    /** The FXML path for the register user page. */
    public static final String PATH = "/frontend/auth/RegisterUser.fxml";
    
    /** TextField for username input. */
    @FXML
    private TextField usernameField;
    
    /** TextField for user name input. */
    @FXML
    private TextField nameField;
    
    /** PasswordField for password input. */
    @FXML
    private PasswordField passwordField;
    
    /** TextField for displaying password (used for visibility toggle). */
    @FXML
    private TextField passwordTextField;
    
    /** PasswordField for confirming the password. */
    @FXML
    private PasswordField passwordConfirmField;
    
    /** TextField for displaying password confirmation (used for visibility toggle). */
    @FXML
    private TextField passwordConfirmTextField;
    
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
     * <p>Listens for changes in the username, password, and password confirmation fields to provide
     * real-time feedback and binds the password visibility toggle to switch between
     * {@link PasswordField} and {@link TextField}.</p>
     */
    @FXML
    private void initialize() {
        usernameField.textProperty().addListener(UserLogPageUtils.textFielsListener(usernameField, errorLabel));
        
        passwordField.textProperty().addListener(UserLogPageUtils.textFielsListener(passwordTextField.getParent(), errorLabel));
        passwordField.textProperty().bindBidirectional(passwordTextField.textProperty());
        
        passwordConfirmField.textProperty().addListener(UserLogPageUtils.textFielsListener(passwordConfirmTextField.getParent(), errorLabel));
        passwordConfirmField.textProperty().bindBidirectional(passwordConfirmTextField.textProperty());
    }
    
    /**
     * Switches to the user login page.
     * <p>Activates the login page, deactivating the current registration page.</p>
     */
    @FXML
    private void changeToLogInUserPage() {
        App.getApplicationInstance().activateLogPage(true);
    }
    
    /**
     * Toggles password visibility based on the CheckBox state.
     * <p>If the CheckBox is selected, displays the password and confirmation password in plain text.
     * If not selected, displays them as masked text.</p>
     *
     * @param event The ActionEvent triggered by the CheckBox.
     */
    @FXML
    void passwordVisibility(ActionEvent event) {
        if (showPassword.isSelected()) {
            passwordTextField.toFront();
            passwordConfirmTextField.toFront();
        } else {
            passwordField.toFront();
            passwordConfirmField.toFront();
        }
    }
    
    /**
     * Handles the user registration process.
     * <p>Creates a new user account with the provided information and handles potential errors,
     * including username already existing, invalid field values, and password mismatches.</p>
     */
    @FXML
    void createAccount() {
        try {
            UserInformationDto registerDto = new UserInformationDto();
            registerDto.setUsername(usernameField.getText());
            registerDto.setName(nameField.getText());
            registerDto.setPassword(passwordField.getText());
            registerDto.setPasswordConfirm(passwordConfirmField.getText());
            this.userAuth.registerUser(registerDto);
            App.getApplicationInstance().closeLogPage();
        } catch (UserAlreadyExistsException e) {
            // This happens when the user account already exists (its username exists for an existing account).
            // We will inform the user.
            passwordField.setText("");
            passwordConfirmField.setText("");
            errorLabel.setText("The user is already existing, use a different username");
        } catch (LogValuesAreIncorrectException e) {
            // One of the inputted field texts is not in a valid format.
            // We will inform the user.
            Set<UserLogInfo> userLogInfo = e.getUserLogInfo();
            if (userLogInfo != null) {
                if (userLogInfo.contains(UserLogInfo.NAME))
                    usernameField.setStyle("-fx-border-color: red");
                if (userLogInfo.contains(UserLogInfo.PASSWORD))
                    passwordField.getParent().setStyle("-fx-border-color: red");
                if (userLogInfo.contains(UserLogInfo.PASSWORD_CONFIRM))
                    passwordConfirmField.getParent().setStyle("-fx-border-color: red");
            }
            errorLabel.setText(e.getMessage());
        } catch (UserPasswordIsIncorrectException e) {
            // Happens when the password does not match the confirm password.
            // We will inform the user.
            passwordField.getParent().setStyle("-fx-border-color: red");
            passwordConfirmField.getParent().setStyle("-fx-border-color: red");
            errorLabel.setText(e.getMessage());
        }
    }
}