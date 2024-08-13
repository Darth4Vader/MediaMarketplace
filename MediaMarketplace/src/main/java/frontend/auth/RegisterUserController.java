package frontend.auth;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.controllers.UserAuthenticateController;
import backend.dto.users.UserInformationDto;
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

@Component
public class RegisterUserController {
	
	public static final String PATH = "/frontend/auth/RegisterUser.fxml";
	
	@FXML
	private TextField usernameField;
	
	@FXML
	private TextField nameField;
	
	@FXML
	private PasswordField passwordField;
	
	@FXML
	private TextField passwordTextField;
	
	@FXML
	private PasswordField passwordConfirmField;
	
	@FXML
	private TextField passwordConfirmTextField;
	
	@FXML
	private Label errorLabel;
	
	@FXML
	private CheckBox showPassword;
	
	@Autowired
	private UserAuthenticateController userAuth;
	
	@FXML
	private void initialize() {
		usernameField.textProperty().addListener(UserLogPageUtils.textFielsListener(usernameField, errorLabel));
		
		passwordField.textProperty().addListener(UserLogPageUtils.textFielsListener(passwordTextField.getParent(), errorLabel));
		passwordField.textProperty().bindBidirectional(passwordTextField.textProperty());
		
		passwordConfirmField.textProperty().addListener(UserLogPageUtils.textFielsListener(passwordConfirmTextField.getParent(), errorLabel));
		passwordConfirmField.textProperty().bindBidirectional(passwordConfirmTextField.textProperty());
	}
	
	@FXML
	private void changeToLogInUserPage() {
		App.getApplicationInstance().activateLogPage(true);
	}
	
	@FXML
	void passwordVisibility(ActionEvent event) {
		if(showPassword.isSelected()) {
			passwordTextField.toFront();
			passwordConfirmTextField.toFront();
		}
		else {
			passwordField.toFront();
			passwordConfirmField.toFront();
		}
	}
	
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
			//This happens when the user account already exists (it's username exists for an existing account)
			//we will inform the user
			passwordField.setText("");
			passwordConfirmField.setText("");
			errorLabel.setText("The user is already existing, use a different username");
		} catch (LogValuesAreIncorrectException e) {
			//One of the inputed field text is not in a valid format
			//we will inform the user
			Set<UserLogInfo> userLogInfo = e.getUserLogInfo();
			if(userLogInfo != null) {
				if(userLogInfo.contains(UserLogInfo.NAME))
					usernameField.setStyle("-fx-border-color: red");
				if(userLogInfo.contains(UserLogInfo.PASWORD))
					passwordField.getParent().setStyle("-fx-border-color: red");
				if(userLogInfo.contains(UserLogInfo.PASSWORD_CONFIRM))
					passwordConfirmField.getParent().setStyle("-fx-border-color: red");
			}
			errorLabel.setText(e.getMessage());
		} catch (UserPasswordIsIncorrectException e) {
			//happens when the password does not match the confirm password
			//we will inform the user
			passwordField.getParent().setStyle("-fx-border-color: red");
			passwordConfirmField.getParent().setStyle("-fx-border-color: red");
			errorLabel.setText(e.getMessage());
		}
	}
}
