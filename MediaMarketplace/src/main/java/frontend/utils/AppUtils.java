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

public class AppUtils {
	
	public static Alert createAlertOfError(String title, String bodyText) {
		return createAlertOfType(AlertType.ERROR, title, bodyText);
	}
	
	public static Alert alertOfError(String title, String bodyText) {
		Alert alert = createAlertOfError(title, bodyText);
		alert.show();
		return alert;
	}
	
	public static Alert createAlertOfInformation(String title, String bodyText) {
		return createAlertOfType(AlertType.INFORMATION, title, bodyText);
	}
	
	public static Alert alertOfInformation(String title, String bodyText) {
		Alert alert = createAlertOfInformation(title, bodyText);
		alert.show();
		return alert;
	}
	
	public static Alert createAlertOfType(AlertType type, String title, String bodyText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(bodyText);
		//set the alert icon to our marketplace icon
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        AppImageUtils.loadAppIconImage(stage);
        return alert;
	}
	
	public static void FullListViewAsGridPage(ListView<MovieRow> movieListView, List<?> list) {
		InitiatelistViewAsGridPage(movieListView);
		UpdatelistViewAsGridPage(movieListView, list);
	}
	
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
	
	public static void UpdatelistViewAsGridPage(ListView<MovieRow> movieListView, List<?> list) {
		movieListView.setItems(null);
		ObservableList<MovieRow> movies = FXCollections.observableArrayList();
		int i = 0;
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
