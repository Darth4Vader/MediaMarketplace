package frontend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import backend.ActivateSpringApplication;
import backend.controllers.UserAuthenticateController;
import backend.dtos.references.MovieReference;
import backend.dtos.users.UserInformationDto;
import backend.exceptions.UserNotLoggedInException;
import frontend.admin.AddMoviePageController;
import frontend.admin.AdminProductPageController;
import frontend.auth.LogInUserController;
import frontend.auth.RegisterUserController;
import frontend.cartPage.CartPageController;
import frontend.homePage.HomePageController;
import frontend.moviePage.MoviePageController;
import frontend.searchPage.SearchPageController;
import frontend.utils.AppUtils;
import frontend.utils.CustomExceptionHandler;
import frontend.utils.UserChangeInterface;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Main application class for managing the JavaFX application lifecycle and user interface transitions.
 * Handles initialization, scene management, and user authentication interactions.
 * 
 * <p>This is the class we need to start in order to activate the MarketPlace Spring server and JavaFX app.</p>
 */
@Component
public class App extends Application {
	
	//use this if activate with JavaFX components
	//--module-path "C:\JavaFX_22.02\lib" --add-modules javafx.controls,javafx.fxml
    
    /**
     * The Spring application context used for dependency injection and managing Spring beans.
     * 
     * <p>This is a class parameter.</p>
     */
    private ConfigurableApplicationContext appContext;
    
    /**
     * The singleton instance of the application.
     */
    private static App applicationInstance;

    /**
     * The primary stage for this application.
     */
    private Stage stage;

    /**
     * The main layout pane for the application.
     */
    private BorderPane appPane;
    
    /**
     * The current JavaFX controllers that are in use.
     */
    private List<Object> currentControllerOfPanes;

    /**
     * Controller for user authentication operations.
     */
    private UserAuthenticateController userAuth;

    /**
     * The stage for login or registration dialogs.
     */
    private Stage userLogStage;
    
    /**
     * JavaFX Controller for the application toolbar.
     * <p>This controller manages the toolbar displayed at the top of the application, providing navigation and actions.</p>
     */
    private AppBarController appBarController;

    /**
     * Retrieves the singleton instance of the application.
     * <p>This method provides access to the single instance of the application, allowing other parts of the code
     * to reference the current running instance of the application.</p>
     * 
     * @return The singleton instance of the application.
     */
    public static App getApplicationInstance() {
        return applicationInstance;
    }

    /**
     * Main entry point for the JavaFX application. Launches the application.
     * <p>This method initializes and launches the JavaFX application. It serves as the entry point for the application,
     * handling command-line arguments if necessary.</p>
     * 
     * @param args Command line arguments.
     * @throws Exception If an error occurs during launch.
     */
    public static void main(String[] args) throws Exception {
        launch(args);
    }

    /**
     * Initializes the application, setting up exception handling and the Spring context.
     * <p>This method sets up exception handling for the application and initializes the Spring application context.
     * It is called before the JavaFX application starts.</p>
     * 
     * @throws Exception If an error occurs during initialization.
     */
    @Override
    public void init() throws Exception {
    	// We will set the uncaught exception handler to a custom handler
        // in order to handle certain exception types with JavaFX
        // For example: caught every user operation a guest tries to activate
        Thread curThread = Thread.currentThread();
        curThread.setUncaughtExceptionHandler(new CustomExceptionHandler(this));
        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(this));
        try {
            applicationInstance = this;
            String[] args = getParameters().getRaw().toArray(new String[0]);
            appContext = ActivateSpringApplication.create(args);
        } catch (Exception e) {
        	Thread thread = Thread.currentThread();
            Platform.runLater(() -> Thread.getDefaultUncaughtExceptionHandler().uncaughtException(thread, e));
        }
    }

    /**
     * Starts the JavaFX application, setting the primary stage and loading the home page.
     * <p>This method is called after initialization. It sets up the primary stage, configures the application icon,
     * and loads the initial home page for display.</p>
     * 
     * @param stage The primary stage for this application.
     */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.currentControllerOfPanes = new ArrayList<>();
        if (appContext != null) {
            userAuth = appContext.getBean(UserAuthenticateController.class);
            // Set the stage icon to our marketplace icon
            AppImageUtils.loadAppIconImage(stage);
            // Load Home Page
            changeAppPanel(HomePageController.PATH);
            stage.show();
        }
    }

    /**
     * Stops the JavaFX application and closes the Spring context.
     * <p>This method is called when the application is stopped. It closes the Spring application context and exits
     * the JavaFX platform.</p>
     */
    @Override
    public void stop() {
        // When stopping the market app, close the rest application and the JavaFX application
        if (appContext != null) {
            appContext.close();
        }
        Platform.exit();
    }

    /**
     * Switches the main panel to the specified FXML file.
     * <p>This method changes the current view by loading and displaying the specified FXML file in the main panel.</p>
     * 
     * @param fxmlPath Path to the FXML file to load.
     */
    public void changeAppPanel(String fxmlPath) {
    	currentControllerOfPanes.clear();
        changeAppPanel(loadFXML(fxmlPath));
    }

    /**
     * Updates the main panel with the given Node component.
     * <p>This method updates the main panel to display the provided Node component. If the main panel has not been
     * initialized yet, it sets up the initial layout and toolbar.</p>
     * 
     * @param component The Node component to display in the main panel.
     */
    public boolean changeAppPanel(Node component) {
        if (appPane == null) {
            appPane = new BorderPane();
            FXMLLoader loader = getFXMLLoader(AppBarController.PATH);
            Parent toolBar = loadFXML(loader);
            if (toolBar != null) {
                appBarController = loader.getController();
                VBox box = new VBox();
                box.setAlignment(Pos.CENTER);
                box.getChildren().add(toolBar);
                appPane.setTop(box);
                Scene scene = new Scene(appPane);
                stage.setScene(scene);
                if(toolBar instanceof Region) {
                	Region toolBarSizes = (Region) toolBar;
	                stage.minWidthProperty().bind(toolBarSizes.widthProperty());
	                stage.minHeightProperty().bind(toolBarSizes.heightProperty());
                }
            }
        }
        if (component != null) {
        	refreshToolBar();
            appPane.setCenter(component);
            return true;
        }
        return false;
    }
    
    /**
     * Refreshes the information displayed in the application’s top navigation bar' like the current logged user name.
     * <p>This method updates the content of the app bar by invoking the {@link AppBarController#loadInformation()} method.
     * It checks if the {@code appBarController} is not null before performing the update to ensure that the app bar 
     * information is only refreshed when the controller is available.</p>
     * 
     * @see AppBarController#loadInformation()
     */
    public void refreshToolBar() {
    	//refresh the components that have current user information.
    	if(!currentControllerOfPanes.isEmpty())
    		for(Object object : currentControllerOfPanes)
    			if(object instanceof UserChangeInterface)
    				((UserChangeInterface) object).refreshAllUserInformationInPane();
    	if(appBarController != null) {
    		appBarController.loadInformation();
    	}
    }

    /**
     * Opens the login or registration page based on the specified parameter.
     * <p>This method creates and shows a modal window for either logging in or registering a user, depending on the
     * value of the logIn parameter.</p>
     * 
     * @param logIn True to show the login page, false to show the registration page.
     */
    public void activateLogPage(boolean logIn) {
        if (userLogStage == null) {
            userLogStage = new Stage();
            AppImageUtils.loadAppIconImage(userLogStage);
            userLogStage.initModality(Modality.APPLICATION_MODAL);
            userLogStage.initOwner(App.getApplicationInstance().getStage());
            userLogStage.setOnCloseRequest(x -> userLogStage = null);
        }
        // Activate the log page depending on the request
        Parent root = loadFXML(logIn ? LogInUserController.PATH : RegisterUserController.PATH);
        if (root != null) {
            Scene scene = new Scene(root);
            userLogStage.setScene(scene);
            userLogStage.show();
        }
    }

    /**
     * Closes the login or registration page if it is open.
     * Shows a welcome message if the user logged in successfully.
     * <p>This method closes the login or registration window if it is open. If a user has successfully logged in,
     * it displays a welcome message with the user's username.</p>
     */
    public void closeLogPage() {
        if (userLogStage != null) {
            boolean showWelcome = userLogStage.getScene() != null;
            userLogStage.setScene(null);
            userLogStage.close();
            if (showWelcome) {
                // Greet the current logged user with a welcome message
                if (userAuth != null) {
                    UserInformationDto userDto = userAuth.getCurrentUserDto();
                    AppUtils.alertOfInformation("User Logged Successfully", "Welcome " + userDto.getUsername());
                }
                refreshToolBar();
            }
        }
    }

    /**
     * Loads the search page and performs a search for the specified movie.
     * <p>This method loads the search page and initiates a search for the specified movie title. The search results
     * are then displayed in the application.</p>
     * 
     * @param searchMovie The movie to search for.
     */
    public void enterSearchPage(String searchMovie) {
        FXMLLoader loader = getFXMLLoader(SearchPageController.PATH);
        Parent root = loadFXML(loader);
        if (root != null) {
            SearchPageController controller = loader.getController();
            controller.searchMovies(searchMovie);
    		currentControllerOfPanes.clear();
            changeAppPanel(root);
        }
    }

    /**
     * Enters either the cart page or add movie page based on the current user's admin status.
     * <p>This method determines if the current user is an admin and directs them to the appropriate page: either
     * the cart page or the add movie page.</p>
     */
    public void enterCartOrAddMovies() {
        // Check which page to enter, depending on if the current user is an admin
        boolean isAdmin = userAuth.isCurrentUserAdmin();
        String panePath;
        if (isAdmin) {
            panePath = AddMoviePageController.PATH;
        } else {
            panePath = CartPageController.PATH;
        }
        changeAppPanel(panePath);
    }

    /**
     * Displays the movie page, including admin-specific product page if applicable.
     * <p>This method displays the movie page, including an admin-specific product page if the current user is an
     * admin. A "Go Back" button is also provided to return to the previous page.</p>
     * 
     * @param movie The movie to display.
     */
    public void enterMoviePage(MovieReference movie) {
        // Check if the current user is an admin
        boolean isAdmin = false;
        try {
            isAdmin = userAuth.isCurrentUserAdmin();
        } catch (UserNotLoggedInException e) {
            // Don't need to handle this exception, let guests view the movie product page.
        }
        List<Object> currentControllers = new ArrayList<>();
        VBox box = new VBox();
        Button goBack = new Button("← Go Back");
        Node previous = appPane.getCenter();
        goBack.setOnAction(event -> {
    		currentControllerOfPanes.removeAll(currentControllers);
    		currentControllers.clear();
        	changeAppPanel(previous);
        });
        box.getChildren().add(goBack);
        if (isAdmin) {
            // Let admin view his own movie page, and the default movie page
            FXMLLoader loader = getFXMLLoader(AdminProductPageController.PATH);
            Parent root = loadFXML(loader);
            if (root != null) {
                AdminProductPageController controller = loader.getController();
                controller.initializeProduct(movie);
                box.getChildren().add(root);
                currentControllers.add(controller);
            }
        }
        FXMLLoader loader = getFXMLLoader(MoviePageController.PATH);
        Parent root = loadFXML(loader);
        if (root != null) {
            MoviePageController controller = loader.getController();
            controller.initializeMovie(movie);
            box.getChildren().add(root);
            currentControllers.add(controller);
            if(changeAppPanel(box))
            	currentControllerOfPanes.addAll(currentControllers);
        }
    }

    /**
     * Gets an FXMLLoader instance configured with the Spring application context.
     * <p>This method creates an FXMLLoader instance and sets its controller factory to use the Spring application
     * context for dependency injection.</p>
     * 
     * @param fxmlPath Path to the FXML file.
     * @return The configured FXMLLoader instance.
     */
    public FXMLLoader getFXMLLoader(String fxmlPath) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(appContext::getBean);
        return loader;
    }

    /**
     * Loads a Parent node using the specified FXML path.
     * <p>This method creates an FXMLLoader for the specified FXML file path and loads the corresponding Parent node.</p>
     * 
     * @param fxmlPath Path to the FXML file.
     * @return The loaded Parent node, or null if an error occurs.
     */
    public Parent loadFXML(String fxmlPath) {
        FXMLLoader loader = getFXMLLoader(fxmlPath);
        return loadFXML(loader);
    }

    /**
     * Loads a Parent node using the given FXMLLoader.
     * <p>This method attempts to load a Parent node from the provided FXMLLoader instance. If an error occurs, it
     * is handled by the custom exception handler.</p>
     * 
     * @param loader The FXMLLoader instance.
     * @return The loaded Parent node, or null if an error occurs.
     */
    private Parent loadFXML(FXMLLoader loader) {
        try {
            return loader.load();
        } catch (IOException e) {
            // If there is an exception with loading the FXML then send an exception to our custom handler, and return null
            Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            return null;
        }
    }

    /**
     * Gets the Spring application context.
     * <p>This method returns the Spring application context, which provides access to Spring-managed beans and services.</p>
     * 
     * @return The application context.
     */
    public ConfigurableApplicationContext getContext() {
        return this.appContext;
    }

    /**
     * Gets the primary stage of the application.
     * <p>This method returns the primary stage of the JavaFX application, which is used to manage the main window.</p>
     * 
     * @return The primary stage.
     */
    public Stage getStage() {
        return this.stage;
    }
}