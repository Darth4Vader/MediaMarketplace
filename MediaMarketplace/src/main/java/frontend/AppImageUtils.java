package frontend;

import java.io.File;
import java.net.URI;
import java.net.URL;

import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AppImageUtils {
	
	public static Image loadImageFromClass(String path) {
		try {
			URL url;
			try {
				//try to read the imgage path from a URL
				url = URI.create(path).toURL();
			}
			catch (Exception e) {
				//if fails, then try to read the path from the file system
				url = new File(path).toURI().toURL();
			}
			String fullPath = url.toExternalForm();
			return new Image(fullPath, true);
		}
		catch (Exception e) {
			//if there is an exception with loading the image, or it is missing then return a null
			return null;
		}
	}
	
	private static Image iconImage;
	private static final String ICON_NAME = "markplace_logo.png";
	
	public static void loadAppIconImage(Stage stage) {
		if(iconImage == null) {
			iconImage = new Image(AppImageUtils.class.getResourceAsStream(ICON_NAME));
		}
		stage.getIcons().add(iconImage);
	}

}
