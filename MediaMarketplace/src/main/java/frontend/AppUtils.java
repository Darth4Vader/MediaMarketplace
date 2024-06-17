package frontend;

import java.io.File;
import java.net.MalformedURLException;

import javafx.scene.image.ImageView;

public class AppUtils {

	public AppUtils() {
		// TODO Auto-generated constructor stub
	}
	
	public static ImageView loadImageFromClass(String path) throws MalformedURLException {
		String fullPath = new File(path).toURI().toURL().toExternalForm();
		return new ImageView(fullPath);
	}

}
