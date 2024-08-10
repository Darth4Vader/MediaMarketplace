package frontend;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import backend.ActivateSpringApplication;
import backend.controllers.UserAuthenticateController;
import backend.dto.users.LogInDto;
import backend.dto.users.LogInResponseDto;
import backend.dto.users.UserInformationDto;
import backend.entities.Movie;
import backend.entities.User;
import backend.exceptions.LogValuesAreIncorrectException;
import backend.exceptions.UserAlreadyExistsException;
import backend.exceptions.UserDoesNotExistsException;
import backend.exceptions.UserNotLoggedInException;
import backend.exceptions.UserPasswordIsIncorrectException;
import frontend.admin.AddMoviePageController;
import frontend.admin.AdminProductPageController;
import frontend.auth.LogInUserController;
import frontend.auth.RegisterUserController;
import frontend.cartPage.CartPageController;
import frontend.homePage.HomePageController;
import frontend.moviePage.MoviePageController;
import frontend.searchPage.SearchPageController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

@Component
public class App extends Application {
	
	//--module-path "C:\JavaFX\lib" --add-modules javafx.controls,javafx.fxml
	
	private ConfigurableApplicationContext appContext;
    private static App applicationInstance;
    
    private Stage stage;
    
    private BorderPane appPane;

    public static App getApplicationInstance() {
        return applicationInstance;
    }
	
	public static void main(String[] args) throws Exception {
		launch(args);
	}
	
	@Override
	public void init() throws Exception {
		applicationInstance = this;
		String[] args = getParameters().getRaw().toArray(new String[0]);
		appContext = ActivateSpringApplication.create(args);
	}
	
	private Stage userLogStage;
	
	@Override
	public void start(Stage stage) {
		//caught every user operation a guest try to activate
		Thread curThread = Thread.currentThread();
		UncaughtExceptionHandler catchExp = curThread.getUncaughtExceptionHandler();
		curThread.setUncaughtExceptionHandler(new CustomExceptionHandler(catchExp, this));
		
		Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(catchExp, this));
		
		/*curThread.setUncaughtExceptionHandler((thread, throwable) -> {
			if(isCausedBy(throwable, UserNotLoggedInException.class)) {
				System.out.println("user is not logged to the system");
		        Alert alert = new Alert(Alert.AlertType.ERROR);
		        alert.setTitle("The user is not logged in");
		        alert.setHeaderText("sign in or continue to browse as unlogged");
		        
		        Button signInBtn = createSignButton(alert, true);
		        Button registerBtn = createSignButton(alert, false);
		        HBox box = new HBox();
		        box.setSpacing(10);
				box.setBorder(new Border(new BorderStroke(Color.PINK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
			            new BorderWidths(1))));
		        //box.prefWidthProperty().bind(alert.widthProperty());
		        //box.setMaxWidth(Double.MAX_VALUE);
		        box.getChildren().addAll(signInBtn, registerBtn);
		        alert.getDialogPane().setContent(box);
		        alert.show();
			}
			else {
				catchExp.uncaughtException(thread, throwable);
			}
			/*Throwable exp = throwable;
			while(exp != null) {
				if(exp instanceof UserNotLoggedInException) {
					break;
				}
				exp = exp.getCause();
			}
			if(exp == null)
				catchExp.uncaughtException(thread, throwable);
			else {
				System.out.println("user is not logged to the system");
			}*/
        //});
		this.stage = stage;
		userAuth = appContext.getBean(UserAuthenticateController.class);
		LogInDto dto = new LogInDto("frodo", "bag");
		//LogInDto dto = new LogInDto("bilbo", "bag");
		try {
			LogInResponseDto d = userAuth.loginUser(dto);
			//userAuth.registerUser(new UserInformationDto("frodo", "", "bag", "bag"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//changeStageToFXML(LogInUserController.PATH);
		
		/*Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				try {
					changeAppPanel(HomePageController.PATH);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});*/
		
		changeAppPanel(HomePageController.PATH);
		
		//changeAppPanel(AddMoviePageController.PATH);
		
		
		//changeAppPanel(SortPageController.PATH);
		
		//changeAppPanel(CartPageController.PATH);
		
		//Don't forget about this
		
		/*ImageView imageView = new ImageView(AppUtils.loadImageFromClass("/frontend/markplace_logo.png"));
	    imageView.setPreserveRatio(true);
	    imageView.setFitWidth(64);
	    imageView.setFitHeight(64);
		stage.getIcons().add(imageView.snapshot(null, null));
		
	    imageView.setFitWidth(32);
	    imageView.setFitHeight(32);
		stage.getIcons().add(imageView.snapshot(null, null));*/
		
		
		stage.getIcons().add(new Image(getClass().getResourceAsStream("markplace_logo.png")));
		
		//changeAppPanel(AddMoviePageController.PATH);
		
		stage.show();
		/*FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontend/auth/RegisterUser.fxml"));
	    //System.out.println(getClass().getResource("/frontend/auth/RegisterUser.fxml"));
	    loader.setControllerFactory(appContext::getBean);
	    Parent root = loader.load();
	    //final Controller controller = loader.getController();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();*/
	}
	
	public void activateLogPage(boolean logIn) {
    	if(userLogStage == null) {
    		userLogStage = new Stage();
    		userLogStage.initModality(Modality.APPLICATION_MODAL);
    		userLogStage.initOwner(App.getApplicationInstance().getStage());
    		userLogStage.setOnCloseRequest(x -> {
    			userLogStage = null;
    		});
    	}
    	Parent root = loadFXML(logIn ? LogInUserController.PATH : RegisterUserController.PATH);
		if(root != null) {
	    	Scene scene = new Scene(root);
			userLogStage.setScene(scene);
			userLogStage.show();
		}
	}
	
	public Parent loadFXML(String fxmlPath) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
		    loader.setControllerFactory(appContext::getBean);
		    return loader.load();
		}
		catch (IOException e) {
			System.out.println("Pro");
			Thread.getDefaultUncaughtExceptionHandler().uncaughtException(null, e);
			return null;
		}
	}
	
	public FXMLLoader getFXMLLoader(String fxmlPath) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
	    loader.setControllerFactory(appContext::getBean);
	    return loader;
	}
	
	public void changeStageToFXML(String fxmlPath) {
		Parent root = loadFXML(fxmlPath);
		if(root != null) {
			Scene scene = new Scene(root);
			stage.setScene(scene);
		}
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
	
	private UserAuthenticateController userAuth;
	
	public Stage getStage() {
		return this.stage;
	}
	
	public void enterSearchPage(String searchMovie) {
		try {
			FXMLLoader loader = getFXMLLoader(SearchPageController.PATH);
			Parent root = loader.load();
			SearchPageController controller = loader.getController();
			controller.searchMovies(searchMovie);
			changeAppPanel(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void enterCartOrAddMovies() {
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
	
	/*public void enterMoviePage(Movie movie) {
		boolean isAdmin = false;
		try {
			isAdmin = userAuth.isCurrentUserAdmin();
		}
		catch (UserNotLoggedInException e) {
			//ok, let guests view the movie product page.
		}
		try {
			System.out.println("In Admin: " + isAdmin);
			Parent root;
			if(isAdmin) {
				FXMLLoader loader = App.getApplicationInstance().getFXMLLoader(AdminProductPageController.PATH);
				root = loader.load();
				AdminProductPageController controller = loader.getController();
				controller.initializeProduct(movie);
			}
			else {
				FXMLLoader loader = App.getApplicationInstance().getFXMLLoader(MoviePageController.PATH);
				root = loader.load();
				MoviePageController controller = loader.getController();
				controller.initializeMovie(movie);
			}
			VBox box = new VBox();
			Button goBack = new Button("← Go Back");
			Node previous = appPane.getCenter();
			goBack.setOnAction(event -> {
				try {
					changeAppPanel(previous);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			box.getChildren().add(goBack);
			box.getChildren().add(root);
			changeAppPanel(box);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}*/
	
	public void enterMoviePage(Movie movie) {
		boolean isAdmin = false;
		try {
			isAdmin = userAuth.isCurrentUserAdmin();
		}
		catch (UserNotLoggedInException e) {
			//ok, let guests view the movie product page.
		}
		try {
			System.out.println("In Admin: " + isAdmin);
			Parent root;
			VBox box = new VBox();
			Button goBack = new Button("← Go Back");
			Node previous = appPane.getCenter();
			goBack.setOnAction(event -> {
				changeAppPanel(previous);
			});
			box.getChildren().add(goBack);
			if(isAdmin) {
				FXMLLoader loader = App.getApplicationInstance().getFXMLLoader(AdminProductPageController.PATH);
				root = loader.load();
				AdminProductPageController controller = loader.getController();
				controller.initializeProduct(movie);
				box.getChildren().add(root);
			}
			FXMLLoader loader = App.getApplicationInstance().getFXMLLoader(MoviePageController.PATH);
			root = loader.load();
			MoviePageController controller = loader.getController();
			controller.initializeMovie(movie);
			box.getChildren().add(root);
			changeAppPanel(box);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void stop() {
		appContext.close();
		Platform.exit();
		//System.exit(0);
	}
}