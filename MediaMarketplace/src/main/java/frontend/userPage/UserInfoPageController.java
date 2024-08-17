package frontend.userPage;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.controllers.UserAuthenticateController;
import backend.dtos.users.UserInformationDto;
import backend.exceptions.LogValuesAreIncorrectException;
import backend.exceptions.UserPasswordIsIncorrectException;
import backend.exceptions.enums.UserLogInfo;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

/**
 * Controller class for managing user information updates on the user info page.
 * Handles the retrieval and update of user details, including username and password.
 */
@Component
public class UserInfoPageController {

    /**
     * Path to the FXML file for the user info page.
     */
    public static final String PATH = "/frontend/userPage/UserInfoPage.fxml";
    
    /**
     * TextField for inputting the username.
     */
    @FXML
    private TextField usernameField;
    
    /**
     * TextField for inputting the user's name.
     */
    @FXML
    private TextField nameField;
    
    /**
     * TextField for inputting the user's password.
     */
    @FXML
    private TextField passwordField;
    
    /**
     * TextField for confirming the user's password.
     */
    @FXML
    private TextField passwordConfirmField;
    
    /**
     * Label for displaying error messages or success notifications.
     */
    @FXML
    private Label errorLabel;
    
    /**
     * Controller for user authentication and information management.
     */
    @Autowired
    private UserAuthenticateController userAuthenticateController;
    
    /**
     * Initializes the user info page with current user details.
     * Sets the username and name fields with the values retrieved from the user authentication controller.
     */
    @FXML
    private void initialize() {
        UserInformationDto userDto = userAuthenticateController.getCurrentUserDto();
        usernameField.setText(userDto.getUsername());
        nameField.setText(userDto.getName());
    }
    
    /**
     * Updates the user information based on the input fields.
     * Validates the input and handles errors by updating the UI with error messages.
     * On success, updates the user details and shows a success message.
     */
    @FXML
    private void updateInformation() {
        UserInformationDto userInfo = new UserInformationDto();
        userInfo.setUsername(usernameField.getText());
        userInfo.setName(nameField.getText());
        userInfo.setPassword(passwordField.getText());
        userInfo.setPasswordConfirm(passwordConfirmField.getText());
        try {
            userAuthenticateController.updateUserInformation(userInfo);
            errorLabel.setTextFill(Color.GREEN);
            errorLabel.setText("User updated successfully");
        } catch (LogValuesAreIncorrectException e) {
            // If the input format is invalid, highlight the incorrect fields and show an error message
            Set<UserLogInfo> userLogInfo = e.getUserLogInfo();
            if (userLogInfo != null) {
                if (userLogInfo.contains(UserLogInfo.NAME))
                    usernameField.setStyle("-fx-border-color: red");
                if (userLogInfo.contains(UserLogInfo.PASSWORD))
                    passwordField.setStyle("-fx-border-color: red");
                if (userLogInfo.contains(UserLogInfo.PASSWORD_CONFIRM))
                    passwordConfirmField.setStyle("-fx-border-color: red");
            }
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText(e.getMessage());
        } catch (UserPasswordIsIncorrectException e) {
            // If the password and confirmation do not match, highlight the fields and show an error message
            passwordField.setStyle("-fx-border-color: red");
            passwordConfirmField.setStyle("-fx-border-color: red");
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText(e.getMessage());
        }
    }
}