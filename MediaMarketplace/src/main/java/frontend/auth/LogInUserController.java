package frontend.auth;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.controllers.UserAuthenticateController;
import backend.dto.users.LogInDto;
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

@Component
public class LogInUserController {
	
	public static final String PATH = "/frontend/auth/LogInUser.fxml";
	
	@FXML
	private TextField nameField;
	
	@FXML
	private PasswordField passwordField;
	
	@FXML
	private TextField passwordTextField;
	
	@FXML
	private Label errorLabel;
	
	@FXML
	private CheckBox showPassword;
	
	@Autowired
	private UserAuthenticateController userAuth;
	
	@FXML
	private void initialize() {
		nameField.textProperty().addListener(UserLogPageUtils.textFielsListener(nameField, errorLabel));
		passwordField.textProperty().addListener(UserLogPageUtils.textFielsListener(passwordTextField.getParent(), errorLabel));
		passwordField.textProperty().bindBidirectional(passwordTextField.textProperty());
	}
	
	@FXML
	private void changeToRegisterUserPage() {
		App.getApplicationInstance().activateLogPage(false);
	}
	
	@FXML
	void passwordVisibility(ActionEvent event) {
		if(showPassword.isSelected())
			passwordTextField.toFront();
		else
			passwordField.toFront();
	}
	
	@FXML
	void createAccount() {
		String name = nameField.getText();
		String password = passwordField.getText();
		try {
			LogInDto dto = new LogInDto(name, password);
			this.userAuth.loginUser(dto);
			App.getApplicationInstance().closeLogPage();
		} catch (UserDoesNotExistsException e) {
			//This happens when the user account does not exists (it's username does not exists)
			//we will inform the user
			passwordField.setText("");
			errorLabel.setText("The user does not exists, use a different username");
		} catch (UserPasswordIsIncorrectException e) {
			//The password does not matches the password of the account that is associated with the username
			//we will inform the user
			passwordField.setText("");
			passwordField.getParent().setStyle("-fx-border-color: red");
			errorLabel.setText("The Password does not match the user password, try a different passsword");
		}
		catch (LogValuesAreIncorrectException e) {
			//One of the inputed field text is not in a valid format
			//we will inform the user
			Set<UserLogInfo> userLogInfo = e.getUserLogInfo();
			if(userLogInfo != null) {
				if(userLogInfo.contains(UserLogInfo.NAME))
					nameField.setStyle("-fx-border-color: red");
				if(userLogInfo.contains(UserLogInfo.PASWORD))
					passwordField.getParent().setStyle("-fx-border-color: red");
			}
			errorLabel.setText(e.getMessage());
		}
	}
}
