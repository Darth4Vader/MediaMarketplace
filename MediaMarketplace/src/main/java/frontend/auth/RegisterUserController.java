package frontend.auth;

import java.io.IOException;
import java.io.Serializable;
import java.util.Set;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import DataStructures.UserLogInfo;
import backend.controllers.UserAuthenticateController;
import backend.dto.users.RegisterDto;
import backend.exceptions.LogValuesAreIncorrectException;
import backend.exceptions.UserAlreadyExistsException;
import frontend.App;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

@Component
public class RegisterUserController implements Serializable {
	
	public static final String PATH = "/frontend/auth/RegisterUser.fxml";
	
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
		App.getApplicationInstance().changeStageToFXML(LogInUserController.PATH);
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
			this.userAuth.registerUser(new RegisterDto(name, password));
		} catch (UserAlreadyExistsException e) {
			nameField.setText("");
			passwordField.setText("");
			errorLabel.setText("The user is already existing, use a different username");
		} catch (LogValuesAreIncorrectException e) {
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
