package frontend.userPage;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.controllers.UserAuthenticateController;
import frontend.App;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

@Component
public class UserPageController {
	
	public static final String PATH = "/frontend/userPage/UserPage.fxml";
	
	@FXML
	private BorderPane mainPane;
	
	@Autowired
	private UserAuthenticateController userAuthenticateController;
	
	@FXML
	private void initialize() {
		userAuthenticateController.authenticateLoggedUser();
	}
	
	@FXML
	private void enterUserInfoPage() {
		setMainUserPanel(UserInfoPageController.PATH);
	}
	
	@FXML
	private void enterOrderHistoryPage() {
		setMainUserPanel(OrderHistoryController.PATH);
	}
	
	@FXML
	private void enterMediaLibraryPage() {
		setMainUserPanel(MediaLibraryController.PATH);
	}
	
	private void setMainUserPanel(String path) {
		userAuthenticateController.authenticateLoggedUser();
		Parent panel =  App.getApplicationInstance().loadFXML(path);
		mainPane.setCenter(panel);
	}
	
}
