package frontend;
import java.io.IOException;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import backend.ActivateSpringApplication;
import backend.controllers.UserAuthenticateController;
import backend.dto.mediaProduct.MovieDto;
import backend.dto.mediaProduct.MovieReference;
import backend.dto.users.UserInformationDto;
import backend.entities.Movie;
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
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

@Component
public class App extends Application {
	
	//--module-path "C:\JavaFX\lib" --add-modules javafx.controls,javafx.fxml
	
	private ConfigurableApplicationContext appContext;
    private static App applicationInstance;
    
    private Stage stage;
    
    private BorderPane appPane;
    
	private UserAuthenticateController userAuth;
	
	private Stage userLogStage;

    public static App getApplicationInstance() {
        return applicationInstance;
    }
	
	public static void main(String[] args) throws Exception {
		launch(args);
	}
	
	@Override
	public void init() {
		//we will set the uncaught exception handler to a custom handler
		//in order to handle certain exception types with JavaFX
		//for example: caught every user operation a guest try to activate
		Thread curThread = Thread.currentThread();
		curThread.setUncaughtExceptionHandler(new CustomExceptionHandler(this));
		Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(this));
		try {
			applicationInstance = this;
			String[] args = getParameters().getRaw().toArray(new String[0]);
			appContext = ActivateSpringApplication.create(args);
		}
		catch (Exception e) {
			Thread thread = Thread.currentThread();
			Platform.runLater(new Runnable() {
			    @Override
			    public void run() {
					Thread.getDefaultUncaughtExceptionHandler().uncaughtException(thread, e);
			    }
			});
		}
	}
	
	@Override
	public void start(Stage stage) {
		this.stage = stage;
		if(appContext != null) {
			userAuth = appContext.getBean(UserAuthenticateController.class);
			//set the stage icon to our marketplace icon
			AppImageUtils.loadAppIconImage(stage);
			//load Home Page
			changeAppPanel(HomePageController.PATH);
			stage.show();
		}
	}
	
	@Override
	public void stop() {
		//when stopping the market app, close the rest application and the JavaFX application
		if(appContext != null) {
			appContext.close();
		}
		Platform.exit();
	}
	
	public void changeAppPanel(String fxmlPath) {
		changeAppPanel(loadFXML(fxmlPath));
	}
	
	public void changeAppPanel(Node component) {
		if(appPane == null) {
			appPane = new BorderPane();
			Parent toolBar = loadFXML("/frontend/AppBar.fxml");
			if(toolBar != null) {
				VBox box = new VBox();
				box.setAlignment(Pos.CENTER);
				box.getChildren().add(toolBar);
				appPane.setTop(box);
				Scene scene = new Scene(appPane);
				stage.setScene(scene);
			}
		}
		if(component != null)
			appPane.setCenter(component);
	}
	
	public void activateLogPage(boolean logIn) {
    	if(userLogStage == null) {
    		userLogStage = new Stage();
    		AppImageUtils.loadAppIconImage(userLogStage);
    		userLogStage.initModality(Modality.APPLICATION_MODAL);
    		userLogStage.initOwner(App.getApplicationInstance().getStage());
    		userLogStage.setOnCloseRequest(x -> {
    			userLogStage = null;
    		});
    	}
    	//Activate the log page depending on the request
    	Parent root = loadFXML(logIn ? LogInUserController.PATH : RegisterUserController.PATH);
		if(root != null) {
	    	Scene scene = new Scene(root);
			userLogStage.setScene(scene);
			userLogStage.show();
		}
	}
	
	public void closeLogPage() {
    	if(userLogStage != null) {
    		boolean showWelcome = userLogStage.getScene() != null;
    		userLogStage.setScene(null);
    		userLogStage.close();
    		if(showWelcome) {
    			//Greet the current logged user with a welcome message
    			if(userAuth != null) {
	    			UserInformationDto userDto = userAuth.getCurrentUserDto();
	    			AppUtils.alertOfInformation("User Logged Successfully", "Welcome " + userDto.getUsername());
    			}
    		}
    	}
	}
	
	public void enterSearchPage(String searchMovie) {
		FXMLLoader loader = getFXMLLoader(SearchPageController.PATH);
		Parent root = loadFXML(loader);
		if(root != null) {
			SearchPageController controller = loader.getController();
			controller.searchMovies(searchMovie);
			changeAppPanel(root);
		}
	}
	
	public void enterCartOrAddMovies() {
		//check which page to enter, depending on if the current user is an admin
		boolean isAdmin = userAuth.isCurrentUserAdmin();
		String panePath;
		if(isAdmin) {
			panePath = AddMoviePageController.PATH;
		}
		else {
			panePath = CartPageController.PATH;
		}
		changeAppPanel(panePath);
	}
	
	public void enterMoviePage(MovieReference movie) {
		//check if the current user is an admin
		boolean isAdmin = false;
		try {
			isAdmin = userAuth.isCurrentUserAdmin();
		}
		catch (UserNotLoggedInException e) {
			//don't need to handle this exception, let guests view the movie product page.
		}
		VBox box = new VBox();
		Button goBack = new Button("← Go Back");
		Node previous = appPane.getCenter();
		goBack.setOnAction(event -> {
			changeAppPanel(previous);
		});
		box.getChildren().add(goBack);
		if(isAdmin) {
			//let admin view his own movie page, and the default movie page
			FXMLLoader loader = getFXMLLoader(AdminProductPageController.PATH);
			Parent root = loadFXML(loader);
			if(root != null) {
				AdminProductPageController controller = loader.getController();
				controller.initializeProduct(movie);
				box.getChildren().add(root);
			}
		}
		FXMLLoader loader = getFXMLLoader(MoviePageController.PATH);
		Parent root = loadFXML(loader);
		if(root != null) {
			MoviePageController controller = loader.getController();
			controller.initializeMovie(movie);
			box.getChildren().add(root);
			changeAppPanel(box);
		}
	}
	
	public FXMLLoader getFXMLLoader(String fxmlPath) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
	    loader.setControllerFactory(appContext::getBean);
	    return loader;
	}
	
	public Parent loadFXML(String fxmlPath) {
		FXMLLoader loader = getFXMLLoader(fxmlPath);
	    return loadFXML(loader);
	}
	
	private Parent loadFXML(FXMLLoader loader) {
		try {
		    return loader.load();
		}
		catch (IOException e) {
			//if there is an exception with loading the FXML then send an exception to our custom handler, and return null
			Thread.getDefaultUncaughtExceptionHandler().uncaughtException(null, e);
			return null;
		}
	}
	
	public ConfigurableApplicationContext getContext() {
		return this.appContext;
	}
	
	public Stage getStage() {
		return this.stage;
	}
}