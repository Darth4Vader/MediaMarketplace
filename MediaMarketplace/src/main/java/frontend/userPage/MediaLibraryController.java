package frontend.userPage;

import java.net.MalformedURLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.controllers.MoviePurchasedController;
import backend.dtos.references.MovieReference;
import frontend.utils.AppUtils;
import frontend.utils.MovieRow;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

/**
 * Controller for the Media Library page, which displays a list of movies purchased by the user.
 */
@Component
public class MediaLibraryController {

    /**
     * The FXML path for the Media Library view.
     */
    public static final String PATH = "/frontend/userPage/MediaLibrary.fxml";

    /**
     * The ListView displaying the movies in the media library.
     */
    @FXML
    private ListView<MovieRow> moviePane;

    /**
     * Controller for managing purchased media products.
     */
    @Autowired
    private MoviePurchasedController mediaPurchasedController;

    /**
     * Initializes the media library view by loading all active media products of the user.
     * 
     * @throws MalformedURLException if there is an issue with the URL formatting.
     */
    @FXML
    private void initialize() throws MalformedURLException {
        List<MovieReference> movies = mediaPurchasedController.getAllActiveMediaProductsOfUser();
        AppUtils.FullListViewAsGridPage(moviePane, movies);
    }
}