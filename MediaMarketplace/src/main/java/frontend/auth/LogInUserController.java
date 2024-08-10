package frontend.auth;

import java.io.IOException;
import java.io.Serializable;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import DataStructures.UserLogInfo;
import backend.controllers.UserAuthenticateController;
import backend.dto.users.LogInDto;
import backend.dto.users.LogInResponseDto;
import backend.exceptions.LogValuesAreIncorrectException;
import backend.exceptions.UserDoesNotExistsException;
import backend.exceptions.UserPasswordIsIncorrectException;
import backend.services.TokenService;
import frontend.App;
import frontend.homePage.HomePageController;
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
public class LogInUserController implements Serializable {
	
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
	
	@Autowired
	private TokenService tokenService;
	
	@FXML
	private void initialize() {
		nameField.textProperty().addListener(textFielsListener(nameField));
		passwordField.textProperty().addListener(textFielsListener(passwordTextField.getParent()));
		passwordField.textProperty().bindBidirectional(passwordTextField.textProperty());
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
	private void changeToLogInUserPage(ActionEvent e) throws IOException {
		e.consume();
		Object object = e.getSource();
		if(object instanceof Button) {
			Button btn = (Button) object;
			final Stage stage = (Stage) btn.getScene().getWindow();
			Parent root = App.getApplicationInstance().loadFXML(RegisterUserController.PATH);
			Scene scene = new Scene(root);
			stage.setScene(scene);
		}
	}*/
	
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
			LogInDto dto =new LogInDto(name, password);
			LogInResponseDto d = this.userAuth.loginUser(dto);
			App.getApplicationInstance().changeAppPanel(HomePageController.PATH);
			//tokenService.invalidateJwt(d.getJwt());
			//System.out.println(tokenService.getCurrentUser(d.getJwt()));
		} catch (UserDoesNotExistsException e) {
			nameField.setText("");
			passwordField.setText("");
			errorLabel.setText("The user does not exists, use a different username");
		} catch (UserPasswordIsIncorrectException e) {
			passwordField.setText("");
			passwordField.getParent().setStyle("-fx-border-color: red");
			errorLabel.setText("The Password does not match the user password, try a different passsword");
		}
		catch (LogValuesAreIncorrectException e) {
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
