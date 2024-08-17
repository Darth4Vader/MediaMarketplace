package frontend.utils;

import java.util.List;

import backend.dtos.ProductDto;
import backend.dtos.references.MovieReference;
import frontend.AppImageUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Utility class for common application functions related to UI operations.
 * Provides methods for creating alerts and managing the display of movie data in a list view.
 */
public class AppUtils {
	
    /**
     * Creates an error alert with the specified title and body text.
     * 
     * @param title the title of the alert
     * @param bodyText the body text of the alert
     * @return an {@link Alert} of type {@link AlertType#ERROR} configured with the specified title and body text
     */
	public static Alert createAlertOfError(String title, String bodyText) {
		return createAlertOfType(AlertType.ERROR, title, bodyText);
	}
	
    /**
     * Shows an error alert with the specified title and body text.
     * 
     * @param title the title of the alert
     * @param bodyText the body text of the alert
     * @return the {@link Alert} that was displayed
     */
	public static Alert alertOfError(String title, String bodyText) {
		Alert alert = createAlertOfError(title, bodyText);
		alert.show();
		return alert;
	}
	
    /**
     * Creates an informational alert with the specified title and body text.
     * 
     * @param title the title of the alert
     * @param bodyText the body text of the alert
     * @return an {@link Alert} of type {@link AlertType#INFORMATION} configured with the specified title and body text
     */
	public static Alert createAlertOfInformation(String title, String bodyText) {
		return createAlertOfType(AlertType.INFORMATION, title, bodyText);
	}
	
    /**
     * Shows an informational alert with the specified title and body text.
     * 
     * @param title the title of the alert
     * @param bodyText the body text of the alert
     * @return the {@link Alert} that was displayed
     */
	public static Alert alertOfInformation(String title, String bodyText) {
		Alert alert = createAlertOfInformation(title, bodyText);
		alert.show();
		return alert;
	}
	
    /**
     * Creates an alert of the specified type with the given title and body text.
     * 
     * @param type the type of alert to create (e.g., {@link AlertType#ERROR} or {@link AlertType#INFORMATION})
     * @param title the title of the alert
     * @param bodyText the body text of the alert
     * @return an {@link Alert} configured with the specified type, title, and body text
     */
	public static Alert createAlertOfType(AlertType type, String title, String bodyText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(bodyText);
		//set the alert icon to our marketplace icon
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        AppImageUtils.loadAppIconImage(stage);
        return alert;
	}
	
    /**
     * Initializes and updates the specified {@link ListView} to display a list of movies as a grid page.
     * 
     * @param movieListView the {@link ListView} to initialize and update
     * @param list the list of movies or movie references to display
     */
	public static void FullListViewAsGridPage(ListView<MovieRow> movieListView, List<?> list) {
		InitiatelistViewAsGridPage(movieListView);
		UpdatelistViewAsGridPage(movieListView, list);
	}
	
    /**
     * Initializes the specified {@link ListView} to display items in a grid format.
     * 
     * @param movieListView the {@link ListView} to initialize
     */
	public static void InitiatelistViewAsGridPage(ListView<MovieRow> movieListView) {
		final int MAX = 5;
		movieListView.setCellFactory(new Callback<ListView<MovieRow>, ListCell<MovieRow>>() {
			
			@Override
			public ListCell<MovieRow> call(ListView<MovieRow> param) {
				return new MovieTableCellEditor(movieListView, MAX);
			}
		});
		movieListView.setSelectionModel(null);
	}
	
	/**
	 * Updates the specified {@link ListView} to display a list of movies or movie references in a grid format.
	 * The grid is composed of rows, each containing a maximum of {@code MAX} movies. 
	 * Each row is represented by an instance of {@link MovieRow}.
	 *
	 * <p>This method clears the existing items in the {@code movieListView} and populates it with new data,
	 * creating a new {@link MovieRow} whenever the number of movies reaches the maximum allowed per row.</p>
	 *
	 * <p>If the list contains movies of different types (e.g., {@link ProductDto} or {@link MovieReference}),
	 * this method will extract the {@link MovieReference} objects and group them into rows accordingly.</p>
	 *
	 * @param movieListView the {@link ListView<MovieRow>} to be updated with the grid layout of movies.
	 * @param list the list of movies or movie references to be displayed. This can include instances of
	 *             {@link ProductDto} or {@link MovieReference}. Each {@link ProductDto} should contain
	 *             a {@link MovieReference} that will be extracted and displayed.
	 */
	public static void UpdatelistViewAsGridPage(ListView<MovieRow> movieListView, List<?> list) {
		movieListView.setItems(null);
		ObservableList<MovieRow> movies = FXCollections.observableArrayList();
		int i = 0;
		//The maximum number of items in one row.
		final int MAX = 5;
		MovieRow movieRow = new MovieRow();
		for(Object object : list) {
			MovieReference movie = null;
			if(object instanceof ProductDto)
				movie = ((ProductDto)object).getMovie();
			else if(object instanceof MovieReference)
				movie = (MovieReference) object;
			if(movie != null) {
				movieRow.add(movie);
				i++;
				if(i == MAX) {
					movies.add(movieRow);
					movieRow = new MovieRow();
					i = 0;
				}
			}
		}
		if(!movieRow.getMovies().isEmpty()) {
			movies.add(movieRow);
		}
		movieListView.setItems(movies);
	}
}