package frontend;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import backend.ActivateSpringApplication;
import backend.controllers.UserAuthenticateController;
import backend.dto.users.LogInDto;
import backend.dto.users.LogInResponseDto;
import backend.exceptions.LogValuesAreIncorrectException;
import backend.exceptions.UserDoesNotExistsException;
import backend.exceptions.UserPasswordIsIncorrectException;
import frontend.auth.LogInUserController;
import frontend.homePage.HomePageController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

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
	
	@Override
	public void start(Stage stage) throws IOException, UserDoesNotExistsException, UserPasswordIsIncorrectException, LogValuesAreIncorrectException {
		this.stage = stage;
		
		UserAuthenticateController userAuth = appContext.getBean(UserAuthenticateController.class);
		LogInDto dto =new LogInDto("bilbo", "bag");
		LogInResponseDto d = userAuth.loginUser(dto);
		//changeStageToFXML(LogInUserController.PATH);
		changeAppPanel(HomePageController.PATH);
		this.stage.show();
		/*FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontend/auth/RegisterUser.fxml"));
	    //System.out.println(getClass().getResource("/frontend/auth/RegisterUser.fxml"));
	    loader.setControllerFactory(appContext::getBean);
	    Parent root = loader.load();
	    //final Controller controller = loader.getController();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();*/
	}
	
	public Parent loadFXML(String fxmlPath) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
	    loader.setControllerFactory(appContext::getBean);
	    return loader.load();
	}
	
	public void changeStageToFXML(String fxmlPath) throws IOException {
		Parent root = loadFXML(fxmlPath);
		Scene scene = new Scene(root);
		stage.setScene(scene);
	}
	
	public void changeAppPanel(String fxmlPath) throws IOException {
		changeAppPanel(loadFXML(fxmlPath));
	}
	
	public void changeAppPanel(Node component) throws IOException {
		if(appPane == null) {
			appPane = new BorderPane();
			Parent toolBar = loadFXML("/frontend/AppBar.fxml");
			appPane.setTop(toolBar);
			Scene scene = new Scene(appPane);
			stage.setScene(scene);
		}
		appPane.setCenter(component);
	}
	
	@Override
	public void stop() throws IOException {
		appContext.close();
		Platform.exit();
	}
}