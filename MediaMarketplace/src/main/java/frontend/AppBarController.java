package frontend;

import org.springframework.stereotype.Component;

import frontend.homePage.HomePageController;
import frontend.userPage.UserPageController;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

@Component
public class AppBarController {
	
	@FXML
	private TextField searchBar;
	
	@FXML
	private void enterCart() {
		App.getApplicationInstance().enterCartOrAddMovies();
	}
	
	@FXML
	private void enterHome() {
		App.getApplicationInstance().changeAppPanel(HomePageController.PATH);
	}
	
	@FXML
	private void enterUserPage() {
		App.getApplicationInstance().changeAppPanel(UserPageController.PATH);
	}
	
	@FXML
	private void search() {
		String text = searchBar.getText();
		App.getApplicationInstance().enterSearchPage(text);
	}
}
