package frontend;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.ConfigurableApplicationContext;

import backend.ActivateSpringApplication;
import frontend.auth.LogInUserController;
import frontend.homePage.HomePageController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class App extends Application {
	
	//--module-path "C:\JavaFX\lib" --add-modules javafx.controls,javafx.fxml
	
	private ConfigurableApplicationContext appContext;
    private static App applicationInstance;
    
    private Stage stage;

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
		//appContext = ActivateSpringApplication.create(args);
	}
	
	@Override
	public void start(Stage stage) throws IOException {
		this.stage = stage;
		changeStageToFXML(HomePageController.PATH
				/*LogInUserController.PATH*/);
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
	    //loader.setControllerFactory(appContext::getBean);
	    return loader.load();
	}
	
	public void changeStageToFXML(String fxmlPath) throws IOException {
		Parent root = loadFXML(fxmlPath);
		Scene scene = new Scene(root);
		stage.setScene(scene);
	}
	
	@Override
	public void stop() throws IOException {
		appContext.close();
		Platform.exit();
	}
}