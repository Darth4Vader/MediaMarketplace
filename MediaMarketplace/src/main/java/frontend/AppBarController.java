package frontend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.DataUtils;
import backend.controllers.UserAuthenticateController;
import backend.dtos.users.UserInformationDto;
import frontend.homePage.HomePageController;
import frontend.userPage.UserPageController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller class for managing interactions in the application’s top navigation bar.
 * <p>Handles user actions such as navigating to different pages and performing searches.</p>
 */
@Component
public class AppBarController {
	
	/**
	 * Path to the FXML file associated with this controller.
	 */
	public static final String PATH = "/frontend/AppBar.fxml";

    /**
     * The text field used for entering search queries.
     * <p>This field is associated with the search bar in the application's top navigation bar.</p>
     */
    @FXML
    private TextField searchBar;
    
    /**
     * Label for displaying the name of the currently logged-in user.
     * <p>This label is used to show personalized greetings or status messages based on the user's login state.</p>
     * 
     * @see #loadInformation()
     */
    @FXML
    private Label userLoggedNameLabel;
    
    /**
     * Controller for handling user authentication and session management.
     * <p>This controller is responsible for managing user login state and providing user information. It is used
     * to retrieve details about the currently logged-in user.</p>
     * 
     * @see UserAuthenticateController
     * @see #loadInformation()
     */
    @Autowired
    private UserAuthenticateController userAuthenticateController;
    
    /**
     * Loads and displays user-specific information in the app bar.
     * <p>This method retrieves information about the currently logged-in user and updates the {@code userLoggedNameLabel}
     * with a personalized greeting. If a user is logged in, it greets them by their name, or as "User" if no name is provided.
     * If no user is logged in, it displays a message indicating that the user is not logged in.</p>
     * 
     * <p>The method attempts to get the current user's details from the {@link UserAuthenticateController}. If an exception
     * occurs (e.g., if no user is logged in), it defaults to displaying a "User Not Logged" message.</p>
     * 
     * @see UserAuthenticateController#getCurrentUserDto()
     * @see DataUtils#isNotBlank(String)
     */
    public void loadInformation() {
    	String userMessage;
    	try {
    		//try to check if the current is user is logged.
    		UserInformationDto userInformationDto = userAuthenticateController.getCurrentUserDto();
    		//If he his logged, then greet him with an Hello message, based on his name
    		//If the user did not add a name, then call him User.
    		String userName = "User";
    		if(userInformationDto != null) {
    			String name = userInformationDto.getName();
    			if(DataUtils.isNotBlank(name)) {
    				userName = name;
    			}
    		}
    		userMessage = "Hello: " + userName;
    	}
    	catch (Exception e) {
    		//if the user is not logged then tell him
    		userMessage = "User Not Logged";
		}
    	userLoggedNameLabel.setText(userMessage);
    }

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