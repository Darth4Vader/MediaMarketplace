package frontend;

import org.springframework.stereotype.Component;

import frontend.homePage.HomePageController;
import frontend.userPage.UserPageController;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Controller class for managing interactions in the application’s top navigation bar.
 * <p>Handles user actions such as navigating to different pages and performing searches.</p>
 */
@Component
public class AppBarController {

    /**
     * The text field used for entering search queries.
     * <p>This field is associated with the search bar in the application's top navigation bar.</p>
     */
    @FXML
    private TextField searchBar;

    /**
     * Navigates to the cart page or add movie page based on the current user's status.
     * <p>This method is called when the user interacts with the cart button on the app bar.
     * It directs the user to the appropriate page based on their role.</p>
     * 
     * @see App#getApplicationInstance()
     */
    @FXML
    private void enterCart() {
        App.getApplicationInstance().enterCartOrAddMovies();
    }

    /**
     * Navigates to the home page of the application.
     * <p>This method is called when the user clicks the home button on the app bar.
     * It updates the main panel to display the home page.</p>
     * 
     * @see HomePageController#PATH
     * @see App#getApplicationInstance()
     */
    @FXML
    private void enterHome() {
        App.getApplicationInstance().changeAppPanel(HomePageController.PATH);
    }

    /**
     * Navigates to the user profile page.
     * <p>This method is called when the user selects the user page option from the app bar.
     * It changes the current view to display the user profile page.</p>
     * 
     * @see UserPageController#PATH
     * @see App#getApplicationInstance()
     */
    @FXML
    private void enterUserPage() {
        App.getApplicationInstance().changeAppPanel(UserPageController.PATH);
    }

    /**
     * Performs a search using the text entered in the search bar.
     * <p>This method is triggered when the user submits a search query.
     * It retrieves the text from the search bar and initiates a search for the entered query.</p>
     * 
     * @see App#getApplicationInstance()
     */
    @FXML
    private void search() {
        String text = searchBar.getText();
        App.getApplicationInstance().enterSearchPage(text);
    }
}