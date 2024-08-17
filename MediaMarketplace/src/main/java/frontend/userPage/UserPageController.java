package frontend.userPage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.controllers.UserAuthenticateController;
import frontend.App;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

/**
 * Controller class for managing the user page layout and navigation.
 * Handles switching between different user-related pages and ensures user authentication.
 */
@Component
public class UserPageController {

    /**
     * Path to the FXML file for the user page layout.
     */
    public static final String PATH = "/frontend/userPage/UserPage.fxml";
    
    /**
     * Main layout pane of the user page.
     */
    @FXML
    private BorderPane mainPane;
    
    /**
     * Controller for user authentication and management.
     */
    @Autowired
    private UserAuthenticateController userAuthenticateController;
    
    /**
     * Initializes the user page by authenticating the logged-in user.
     * Ensures that the user is authenticated when the page is loaded.
     */
    @FXML
    private void initialize() {
        userAuthenticateController.authenticateLoggedUser();
    }
    
    /**
     * Navigates to the user information page.
     * Loads the user info page into the main pane.
     */
    @FXML
    private void enterUserInfoPage() {
        setMainUserPanel(UserInfoPageController.PATH);
    }
    
    /**
     * Navigates to the order history page.
     * Loads the order history page into the main pane.
     */
    @FXML
    private void enterOrderHistoryPage() {
        setMainUserPanel(OrderHistoryController.PATH);
    }
    
    /**
     * Navigates to the media library page.
     * Loads the media library page into the main pane.
     */
    @FXML
    private void enterMediaLibraryPage() {
        setMainUserPanel(MediaLibraryController.PATH);
    }
    
    /**
     * Loads a specified FXML page into the main pane.
     * Authenticates the user before loading the new page.
     * 
     * @param path the path to the FXML file of the page to load
     */
    private void setMainUserPanel(String path) {
        userAuthenticateController.authenticateLoggedUser();
        Parent panel = App.getApplicationInstance().loadFXML(path);
        if (panel != null) {
            mainPane.setCenter(panel);
        }
    }
}