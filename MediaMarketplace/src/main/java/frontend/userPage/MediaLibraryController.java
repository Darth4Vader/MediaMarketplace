package frontend.userPage;

import java.net.MalformedURLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.controllers.MoviePurchasedController;
import backend.entities.Movie;
import frontend.AppUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

@Component
public class MediaLibraryController {
	
	public static final String PATH = "/frontend/userPage/MediaLibrary.fxml";
	
	@FXML
	private ScrollPane movieScroll;
	
	@FXML
	private GridPane moviePane;
	
	@Autowired
	private MoviePurchasedController mediaPurchasedController;

	@FXML
	private void initialize() throws MalformedURLException {
		List<Movie> movies = mediaPurchasedController.getAllActiveMediaProductsOfUser();
		AppUtils.loadMoviesToGridPane(movies, moviePane, movieScroll);
	}
	
	
}
