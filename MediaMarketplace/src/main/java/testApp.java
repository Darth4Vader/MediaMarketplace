import java.io.IOException;

import backend.ActivateSpringApplication;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class testApp extends Application {

	public static void main(String[] args) throws Exception {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws IOException {
		Parent toolBar = FXMLLoader.load(getClass().getResource("/frontend/admin/AdminProductPage.fxml"));
		Scene scene = new Scene(toolBar);
		stage.setScene(scene);
		stage.show();
	}

}
