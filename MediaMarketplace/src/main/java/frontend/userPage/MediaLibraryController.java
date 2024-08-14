package frontend.userPage;

import java.net.MalformedURLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.controllers.MoviePurchasedController;
import backend.dto.mediaProduct.MovieReference;
import frontend.utils.AppUtils;
import frontend.utils.MovieRow;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

@Component
public class MediaLibraryController {
	
	public static final String PATH = "/frontend/userPage/MediaLibrary.fxml";
	
	@FXML
	private ListView<MovieRow> moviePane;
	
	@Autowired
	private MoviePurchasedController mediaPurchasedController;

	@FXML
	private void initialize() throws MalformedURLException {
		List<MovieReference> movies = mediaPurchasedController.getAllActiveMediaProductsOfUser();
		AppUtils.FullListViewAsGridPage(moviePane, movies);
	}
	
	
}
