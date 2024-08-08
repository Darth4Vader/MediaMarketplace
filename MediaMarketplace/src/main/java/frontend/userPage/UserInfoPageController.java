package frontend.userPage;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import DataStructures.UserLogInfo;
import backend.controllers.UserAuthenticateController;
import backend.dto.users.UserInformationDto;
import backend.exceptions.LogValuesAreIncorrectException;
import backend.exceptions.UserPasswordIsIncorrectException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

@Component
public class UserInfoPageController {
	
	public static final String PATH = "/frontend/userPage/UserInfoPage.fxml";
	
	@FXML
	private TextField usernameField;
	
	@FXML
	private TextField nameField;
	
	@FXML
	private TextField passwordField;
	
	@FXML
	private TextField passwordConfirmField;
	
	@FXML
	private Label errorLabel;
	
	@Autowired
	private UserAuthenticateController userAuthenticateController;
	
	@FXML
	private void initialize() {
		UserInformationDto userDto = userAuthenticateController.getCurrentUserDto();
		usernameField.setText(userDto.getUsername());
		nameField.setText(userDto.getName());
	}
	
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
			errorLabel.setText("User updated successfullys");
		} catch (LogValuesAreIncorrectException e) {
			//if one of the fields text format is not valid, then we will notify the user the problem
			Set<UserLogInfo> userLogInfo = e.getUserLogInfo();
			if(userLogInfo != null) {
				if(userLogInfo.contains(UserLogInfo.NAME))
					usernameField.setStyle("-fx-border-color: red");
				if(userLogInfo.contains(UserLogInfo.PASWORD))
					passwordField.setStyle("-fx-border-color: red");
				if(userLogInfo.contains(UserLogInfo.PASSWORD_CONFIRM))
					passwordConfirmField.setStyle("-fx-border-color: red");
			}
			errorLabel.setTextFill(Color.RED);
			errorLabel.setText(e.getMessage());
		} catch (UserPasswordIsIncorrectException e) {
			//if the password is not matching the password confirm, then we will notify the user.
			passwordField.setStyle("-fx-border-color: red");
			passwordConfirmField.setStyle("-fx-border-color: red");
			errorLabel.setTextFill(Color.RED);
			errorLabel.setText(e.getMessage());
		} 
	}
}
