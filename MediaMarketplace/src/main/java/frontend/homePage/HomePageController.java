package frontend.homePage;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import backend.controllers.MediaProductController;
import backend.entities.MediaProduct;
import backend.services.MediaProductService;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

@Component
public class HomePageController {
	
	public static final String PATH = "/frontend/homePage/HomePage.fxml";
	
	@FXML
	private GridPane gridPane;
	
	@Autowired
	private MediaProductController productController;

	@FXML
	private void initialize() throws MalformedURLException {
		//List<MediaProduct> resp = productController.getAllMediaProducts();
		final int cols = gridPane.getColumnCount();
		int row = 0;
		int currentCols = 0;
		String[] list = {"file:/C:/Users/itay5/git/MediaMarketplace/MediaMarketplace/posters/tt4154756.jpg",
				"file:/C:/Users/itay5/git/MediaMarketplace/MediaMarketplace/posters/tt4154796.jpg",
				"file:/C:/Users/itay5/git/MediaMarketplace/MediaMarketplace/posters/tt2395427.jpg",
				"file:/C:/Users/itay5/git/MediaMarketplace/MediaMarketplace/posters/tt0096895.jpg",
				"file:/C:/Users/itay5/git/MediaMarketplace/MediaMarketplace/posters/tt0103776.jpg",
				"file:/C:/Users/itay5/git/MediaMarketplace/MediaMarketplace/posters/tt0372784.jpg"};
		for(String path : list) {
			ImageView view = new ImageView(path);
			gridPane.add(view, currentCols, row);
			if(currentCols < cols)
				currentCols++;
			else {
				currentCols = 0;
				row++;
				break;
			}
		}
		/*for(MediaProduct product : resp) {
			//String path = getClass().getResource(null);
			File file = new File(product.getImagePath());
			//System.out.println(file.getAbsolutePath());
			System.out.println(file.toURI().toURL().toExternalForm());
			ImageView view = new ImageView(file.toURI().toURL().toExternalForm());
			gridPane.add(view, currentCols, row);
			if(currentCols < cols)
				currentCols++;
			else {
				currentCols = 0;
				row++;
				break;
			}
		}*/
	}

}
