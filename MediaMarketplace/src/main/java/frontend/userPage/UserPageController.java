package frontend.userPage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.DataUtils;
import backend.controllers.UserAuthenticateController;
import backend.dtos.users.UserInformationDto;
import frontend.App;
import frontend.homePage.HomePageController;
import frontend.utils.AppUtils;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

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
     * After that greet the user with a Welcome message calling him by his username.
     */
    @FXML
    private void initialize() {
        UserInformationDto userDto = userAuthenticateController.getCurrentUserDto();
        String welcomeMessage = "Welcome User \"" + userDto.getUsername() + "\" In to your User Page";
        Label welcomeLabel = new Label(welcomeMessage);
        Button signOut = new Button("Sign Out");
        signOut.setOnAction(e -> {
        	try {
        		userAuthenticateController.signOutFromCurrentUser();
        		//if successful then notify the user.
        		String username = userDto.getUsername();
        		String message;
        		if(DataUtils.isNotBlank(username))
        			message = "The user \"" + username + "\"";
        		else
        			message = "The current user";
        		message += " has logged out successfully";
        		Alert alert = AppUtils.createAlertOfInformation("User Logged Out", message);
        		alert.showAndWait();
        		App.getApplicationInstance().changeAppPanel(HomePageController.PATH);
        	}
        	catch (Exception exp) {
        		//if there is a problem, then let the uncaught exception manager to handle it.
				throw exp;
			}
        });
        VBox mainUserPane = new VBox();
        mainUserPane.setSpacing(7);
        mainUserPane.setAlignment(Pos.CENTER);
        mainUserPane.getChildren().addAll(welcomeLabel, signOut);
        mainPane.setCenter(mainUserPane);
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