package frontend.auth;

import java.io.IOException;
import java.io.Serializable;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import DataStructures.UserLogInfo;
import backend.controllers.UserAuthenticateController;
import backend.dto.users.UserInformationDto;
import backend.exceptions.LogValuesAreIncorrectException;
import backend.exceptions.UserAlreadyExistsException;
import backend.exceptions.UserPasswordIsIncorrectException;
import backend.repositories.UserRepository;
import frontend.App;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

@Component
public class RegisterUserController implements Serializable {
	
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
		usernameField.textProperty().addListener(textFielsListener(usernameField));
		
		passwordField.textProperty().addListener(textFielsListener(passwordTextField.getParent()));
		passwordField.textProperty().bindBidirectional(passwordTextField.textProperty());
		
		passwordConfirmField.textProperty().addListener(textFielsListener(passwordConfirmTextField.getParent()));
		passwordConfirmField.textProperty().bindBidirectional(passwordConfirmTextField.textProperty());
	}
	
	private ChangeListener<String> textFielsListener(Node textField) {
		return new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(oldValue.trim().isEmpty() && !newValue.trim().isEmpty()) {
					textField.setStyle("");
					if(!errorLabel.getText().isEmpty())
						errorLabel.setText("");
				}
			}
		};
	}
	
	/*@FXML
	private void changeToRegisterUserPage(ActionEvent e) throws IOException {
		e.consume();
		Object object = e.getSource();
		if(object instanceof Button) {
			Button btn = (Button) object;
			final Stage stage = (Stage) btn.getScene().getWindow();
			Parent root = App.getApplicationInstance().loadFXML(LogInUserController.PATH);
			Scene scene = new Scene(root);
			stage.setScene(scene);
		}
	}*/
	
	@FXML
	private void changeToLogInUserPage() throws IOException {
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
	
	@Autowired
	private UserRepository userRepository;
	
	@FXML
	void createAccount() {
		try {
			UserInformationDto registerDto = new UserInformationDto();
			registerDto.setUsername(usernameField.getText());
			registerDto.setName(nameField.getText());
			registerDto.setPassword(passwordField.getText());
			registerDto.setPasswordConfirm(passwordConfirmField.getText());
			this.userAuth.registerUser(registerDto);
		} catch (UserAlreadyExistsException e) {
			usernameField.setText("");
			passwordField.setText("");
			passwordConfirmField.setText("");
			errorLabel.setText("The user is already existing, use a different username");
		} catch (LogValuesAreIncorrectException e) {
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
			passwordField.getParent().setStyle("-fx-border-color: red");
			passwordConfirmField.getParent().setStyle("-fx-border-color: red");
			errorLabel.setText(e.getMessage());
		}
	}
}
