package frontend.searchPage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.DataUtils;
import backend.controllers.GenreController;
import backend.dtos.references.MovieReference;
import frontend.searchPage.utils.SearchUtils;
import frontend.searchPage.utils.SortDto;
import frontend.utils.AppUtils;
import frontend.utils.MovieRow;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

/**
 * Controller class for the search page of the application.
 * <p>This controller handles user input for searching movies, including filtering by genre, year, and rating. It updates the UI with search results.</p>
 */
@Component
public class SearchPageController {
	
	/**
	 * The path to the FXML file that defines the layout for the search page.
	 * This file is located within the `/frontend/searchPage/` directory and is named `SearchPage.fxml`.
	 * It is used to load and initialize the user interface for the search functionality.
	 */
	public static final String PATH = "/frontend/searchPage/SearchPage.fxml";
	
	@FXML
	private Label searchResultsLabel;
	
	/** TextField for entering movie names to search. */
	@FXML
	private TextField nameField;
	
	/** ComboBox for selecting movie genres. */
	@FXML
	private ComboBox<String> chooseGenres;
	
	/** ListView displaying the selected genres. */
	@FXML
	private ListView<String> genresSelectedPane;
	
	/** ListView displaying the search results for movies. */
	@FXML
	private ListView<MovieRow> movieResultPane;
	
	/** HBox containing the main UI components of the search page. */
	@FXML
	private HBox mainPane;
	
	/** TextField for entering the minimum year for the search filter. */
	@FXML
	private TextField yearAbove;
	
	/** TextField for entering the maximum year for the search filter. */
	@FXML
	private TextField yearBelow;
	
	/** TextField for entering the minimum rating for the search filter. */
	@FXML
	private TextField ratingAbove;
	
	/** TextField for entering the maximum rating for the search filter. */
	@FXML
	private TextField ratingBelow;
	
	/** Controller for managing genres. */
	@Autowired
	private GenreController genreController;
	
	/** FilteredList of genres used for dynamic search suggestions. */
	private FilteredList<String> genresList;
	
	/** ObservableList of selected genres for filtering. */
	private ObservableList<String> genresSelected;
	
	/**
	 * Initializes the search page controller.
	 * <p>Sets up the UI components, binds listeners to the text fields, and populates the genre ComboBox with genres retrieved from the {@link GenreController}.</p>
	 */
	@FXML
	private void initialize() {
		genresSelected = FXCollections.observableArrayList();
        genresSelectedPane.setCellFactory(x -> new SelectedGenreListCell());
        genresSelectedPane.setItems(genresSelected);
        
		yearAbove.textProperty().addListener(SearchPageUtils.textPropertyChangeListener(yearAbove, 4));
		yearBelow.textProperty().addListener(SearchPageUtils.textPropertyChangeListener(yearBelow, 4));
		ratingAbove.textProperty().addListener(SearchPageUtils.ratingChangeListener(ratingAbove));
		ratingBelow.textProperty().addListener(SearchPageUtils.ratingChangeListener(ratingBelow));
		
		chooseGenres.setCellFactory(listView -> new GenreListCell());
		chooseGenres.setButtonCell(new GenreListCell());
		chooseGenres.setEditable(true);
		chooseGenres.setConverter(new StringConverter<String>() {
			
			@Override
			public String toString(String object) {
				return object != null ? object : ""; 
			}
			
			@Override
			public String fromString(String string) {
                if (DataUtils.isBlank(string))
                    return null;
                for (String genre : chooseGenres.getItems())
                    if(genre.equalsIgnoreCase(string))
                        return genre;
                return null;
			}
		});
		List<String> genres = genreController.getAllGenres();
		ObservableList<String> list = FXCollections.observableArrayList(genres);
		genresList = new FilteredList<>(list);
		chooseGenres.getEditor().textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				Platform.runLater(() -> genresList.setPredicate(new Predicate<String>() {

					@Override
					public boolean test(String g) {
						if(genresSelected.contains(g))
							return false;
		                // If filter text is empty, display all genres.
						if(DataUtils.isBlank(newValue))
		                    return true;
		                String lowerCaseFilter = newValue.toLowerCase();
		                return g.toLowerCase().contains(lowerCaseFilter);
					}
	            }));
			}
		});
	    chooseGenres.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(newValue != null) {
					genresSelected.add(newValue);
					chooseGenres.getEditor().setText("");
				}
			}
	      });
		/*
        // 3. Wrap the FilteredList in a SortedList. 
        SortedList<String> sortedData = new SortedList<>(filteredData);
        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(chooseGenres.comparatorProperty());
        // 5. Add sorted (and filtered) data to the table.
        */
        chooseGenres.setItems(genresList);
        // Initialize the movie result pane as a grid.
        AppUtils.InitiatelistViewAsGridPage(movieResultPane);
	}
	
	/**
	 * Removes the specified genre from the list of selected genres.
	 * <p>This method updates the genre filter by removing the genre and refreshing the genre list view.</p>
	 * 
	 * @param genre The genre to remove from the selected genres.
	 */
	private void removeGenreFromSelected(String genre) {
		genresSelected.remove(genre);
		chooseGenres.getEditor().setText("");
		Platform.runLater(() -> genresList.setPredicate(new Predicate<String>() {
			@Override
			public boolean test(String g) {
				return !genresSelected.contains(g);
			}
        }));
	}
	
	/**
	 * Searches for movies based on the provided search text.
	 * <p>This method creates a {@link SortDto} with the search text and invokes the search operation.</p>
	 * 
	 * @param text The text to search for in movie names.
	 */
	public void searchMovies(String text) {
		//Set the advanced search text to the same as the regular search text.
		nameField.setText(text);
		SortDto sortDto = new SortDto();
		sortDto.setName(text);
		searchMovies(sortDto);
	}
	
	/**
	 * Searches for movies based on the provided {@link SortDto} criteria.
	 * <p>This method performs the search operation and updates the movie result pane with the results.</p>
	 * 
	 * @param sortDto The {@link SortDto} containing search criteria and filters.
	 */
	private void searchMovies(SortDto sortDto) {
		List<MovieReference> searchList = SearchUtils.searchMoviesSort(sortDto);
		//tell the user how many results where found, and if none were.
		String searchResultText;
		if(searchList != null && !searchList.isEmpty())
			searchResultText = ""+searchList.size();
		else
			searchResultText = "No Results were found";
		searchResultsLabel.setText(searchResultText);
		AppUtils.UpdatelistViewAsGridPage(movieResultPane, searchList);
	}
	
	/**
	 * Handles the search operation based on user input from various fields.
	 * <p>This method gathers user input from the text fields and selected genres, creates a {@link SortDto} object, and performs the search.</p>
	 */
	@FXML
	private void searchMovies() {
		SortDto sortDto = new SortDto();
		sortDto.setName(nameField.getText());
		List<String> genresNames = new ArrayList<>(genresSelected);
		sortDto.setGenres(genresNames);
		sortDto.setYearAbove(DataUtils.getNumber(yearAbove.getText()));
		sortDto.setYearBelow(DataUtils.getNumber(yearBelow.getText()));
		sortDto.setRatingAbove(DataUtils.getNumber(ratingAbove.getText()));
		sortDto.setRatingBelow(DataUtils.getNumber(ratingBelow.getText()));
		searchMovies(sortDto);
	}
	
	/**
	 * Custom cell renderer for genres in the {@link ComboBox}.
	 * <p>This cell renderer displays genre names in the {@link ComboBox}.</p>
	 */
	private static class GenreListCell extends ListCell<String> {

	    @Override
	    public void updateItem(String item, boolean empty) {
	        super.updateItem(item, empty);
	        if(item == null || empty) {
	            setText(null);
	        } else {
	            setText(item);
	        }
	    }

	}
	
	/**
	 * Custom cell renderer for selected genres in the {@link ListView}.
	 * <p>This cell renderer displays selected genre names with a remove button.</p>
	 */
	private class SelectedGenreListCell extends ListCell<String> {

		private BorderPane pane;
		private Label name;
		private Button remove;
		
		/**
		 * Constructs a new {@link SelectedGenreListCell} with a layout containing a genre name and a remove button.
		 */
		public SelectedGenreListCell() {
			pane = new BorderPane();
			remove = new Button("x");
			remove.setTextFill(Color.RED);
			pane.setLeft(remove);
			name = new Label();
			pane.setCenter(name);
		}
		
	    @Override
	    public void updateItem(String item, boolean empty) {
	        super.updateItem(item, empty);
	        if(item == null || empty) {
	        	setGraphic(null);
	            remove.setOnAction(null);
	            name.setText("");
	        } else {
	        	name.setText(item);
	        	remove.setOnAction(e -> removeGenreFromSelected(item));
	        	setGraphic(pane);
	        }
	    }
	}
}