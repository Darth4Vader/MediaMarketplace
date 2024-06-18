package frontend.userPage;

import java.io.IOException;

import org.springframework.stereotype.Component;

import frontend.App;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

@Component
public class UserPageController {
	
	public static final String PATH = "/frontend/userPage/UserPage.fxml";
	
	@FXML
	private BorderPane mainPane;
	
	@FXML
	private void enterUserInfoPage() throws IOException {
		setMainUserPanel(UserInfoPageController.PATH);
	}
	
	@FXML
	private void enterOrderHistoryPage() throws IOException {
		setMainUserPanel(OrderHistoryController.PATH);
	}
	
	@FXML
	private void enterMediaLibraryPage() throws IOException {
		setMainUserPanel(MediaLibraryController.PATH);
	}
	
	private void setMainUserPanel(String path) throws IOException {
		Parent panel =  App.getApplicationInstance().loadFXML(path);
		mainPane.setCenter(panel);
	}
	
}
