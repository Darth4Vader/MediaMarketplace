package frontend.searchPage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.DataUtils;
import backend.controllers.GenreController;
import backend.entities.Genre;
import backend.entities.Movie;
import frontend.AppUtils;
import frontend.MovieRow;
import frontend.searchPage.utils.SearchUtils;
import frontend.searchPage.utils.SortDto;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

@Component
public class SearchPageController {
	
	public static final String PATH = "/frontend/searchPage/SearchPage.fxml";
	
	@FXML
	private TextField nameField;
	
	@FXML
	private ComboBox<Genre> chooseGenres;
	
	@FXML
	private ListView<Genre> genresSelectedPane;
	
	@FXML
	private ListView<MovieRow> movieResultPane;
	
	@FXML
	private HBox mainPane;
	
	@FXML
	private TextField yearUp;
	
	@FXML
	private TextField yearDown;
	
	@FXML
	private TextField ratingUp;
	
	@FXML
	private TextField ratingDown;
	
	@Autowired
	private GenreController genreController;
	
	private FilteredList<Genre> genresList;
	private ObservableList<Genre> genresSelected;
	
	@FXML
	private void initialize() {
		genresSelected = FXCollections.observableArrayList();
        genresSelectedPane.setCellFactory(x -> new SelectedGenreListCell());
        genresSelectedPane.setItems(genresSelected);
        
		yearUp.textProperty().addListener(textPropertyChangeListener(yearUp, 4));
		yearDown.textProperty().addListener(textPropertyChangeListener(yearDown, 4));
		ratingUp.textProperty().addListener(textPropertyChangeListener(ratingUp, 3));
		ratingDown.textProperty().addListener(textPropertyChangeListener(ratingDown, 3));
		
		chooseGenres.setCellFactory(listView  -> new GenreListCell());
		chooseGenres.setButtonCell(new GenreListCell());
		chooseGenres.setEditable(true);
		chooseGenres.setConverter(new StringConverter<Genre>() {
			
			@Override
			public String toString(Genre object) {
				return object != null ? object.getName() : ""; 
			}
			
			@Override
			public Genre fromString(String string) {
                if (DataUtils.isBlank(string))
                    return null;
                for (Genre genre : chooseGenres.getItems())
                    if(genre.getName().equalsIgnoreCase(string))
                        return genre;
                return null;
			}
		});
		List<Genre> genres = genreController.getAllGenres();
		ObservableList<Genre> list = FXCollections.observableArrayList(genres);
		genresList = new FilteredList<>(list);
		chooseGenres.getEditor().textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				Platform.runLater(() -> genresList.setPredicate(new Predicate<Genre>() {

					@Override
					public boolean test(Genre g) {
						if(genresSelected.contains(g))
							return false;
		                // If filter text is empty, display all persons.
						if(DataUtils.isBlank(newValue))
		                    return true;
		                String lowerCaseFilter = newValue.toLowerCase();
		                if(g.getName().toLowerCase().contains(lowerCaseFilter)) {
		                    return true;
		                }
		                return false;
					}
	            }));
			}
		});
	    chooseGenres.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Genre>() {

			@Override
			public void changed(ObservableValue<? extends Genre> observable, Genre oldValue, Genre newValue) {
				if(newValue != null) {
					genresSelected.add(newValue);
					chooseGenres.getEditor().setText("");
				}
			}
	      });
		/*
        // 3. Wrap the FilteredList in a SortedList. 
        SortedList<Genre> sortedData = new SortedList<>(filteredData);
        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(chooseGenres.comparatorProperty());
        // 5. Add sorted (and filtered) data to the table.
        */
        chooseGenres.setItems(genresList);
        //initiate the movie result pane
        AppUtils.InitiatelistViewAsGridPage(movieResultPane);
	}
	
	private void removeGenreFromSelected(Genre genre) {
		genresSelected.remove(genre);
		chooseGenres.getEditor().setText("");
		Platform.runLater(() -> genresList.setPredicate(new Predicate<Genre>() {
			@Override
			public boolean test(Genre g) {
				if(genresSelected.contains(g))
					return false;
                return true;
			}
        }));
	}
	
	public void searchMovies(String text) {
		SortDto sortDto = new SortDto();
		sortDto.setName(text);
		searchMovies(sortDto);
	}
	
	private void searchMovies(SortDto sortDto) {
		List<Movie> searchList = SearchUtils.searchMoviesSort(sortDto);
		AppUtils.UpdatelistViewAsGridPage(movieResultPane, searchList);
	}
	
	@FXML
	private void searchMovies() {
		SortDto sortDto = new SortDto();
		sortDto.setName(nameField.getText());
		List<String> genresNames = new ArrayList<>();
		for(Genre genre : genresSelected)
			genresNames.add(genre.getName());
		sortDto.setGenres(genresNames);
		sortDto.setYearUp(DataUtils.getNumber(yearUp.getText()));
		sortDto.setYearDown(DataUtils.getNumber(yearDown.getText()));
		sortDto.setRatingUp(DataUtils.getNumber(ratingUp.getText()));
		sortDto.setRatingDown(DataUtils.getNumber(ratingDown.getText()));
		searchMovies(sortDto);
	}
	
	public static ChangeListener<String> textPropertyChangeListener(TextInputControl control, int maxCharacters) {
		return new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		        if (!newValue.matches("\\d*"))
		        	newValue = newValue.replaceAll("[^\\d]", "");
	        	if(newValue.length() > maxCharacters)
	        		newValue = newValue.substring(0, maxCharacters);
		        control.setText(newValue);
		    }
	    };
	}
	
	private static class GenreListCell extends ListCell<Genre> {

	    @Override
	    public void updateItem(Genre item, boolean empty) {
	        super.updateItem(item, empty);
	        if(item == null || empty)
	            setText(null);
	        else
	        	setText(item.getName());
	    }

	}
	
	private class SelectedGenreListCell extends ListCell<Genre> {

		private BorderPane pane;
		private Label name;
		private Button remove;
		
		public SelectedGenreListCell() {
			pane = new BorderPane();
			remove = new Button("x");
			remove.setTextFill(Color.RED);
			pane.setLeft(remove);
			name = new Label();
			pane.setCenter(name);
		}
		
	    @Override
	    public void updateItem(Genre item, boolean empty) {
	        super.updateItem(item, empty);
	        if(item == null || empty) {
	        	setGraphic(null);
	            remove.setOnAction(null);
	            name.setText("");
	        }
	        else {
	        	name.setText(item.getName());
	        	remove.setOnAction(e -> {
	        		removeGenreFromSelected(item);
	        	});
	        	setGraphic(pane);
	        }
	    }
	}
	
}
