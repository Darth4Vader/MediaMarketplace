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

@Component
public class SearchPageController {
	
	public static final String PATH = "/frontend/searchPage/SearchPage.fxml";
	
	@FXML
	private TextField nameField;
	
	@FXML
	private ComboBox<String> chooseGenres;
	
	@FXML
	private ListView<String> genresSelectedPane;
	
	@FXML
	private ListView<MovieRow> movieResultPane;
	
	@FXML
	private HBox mainPane;
	
	@FXML
	private TextField yearAbove;
	
	@FXML
	private TextField yearBelow;
	
	@FXML
	private TextField ratingAbove;
	
	@FXML
	private TextField ratingBelow;
	
	@Autowired
	private GenreController genreController;
	
	private FilteredList<String> genresList;
	private ObservableList<String> genresSelected;
	
	@FXML
	private void initialize() {
		genresSelected = FXCollections.observableArrayList();
        genresSelectedPane.setCellFactory(x -> new SelectedGenreListCell());
        genresSelectedPane.setItems(genresSelected);
        
		yearAbove.textProperty().addListener(SearchPageUtils.textPropertyChangeListener(yearAbove, 4));
		yearBelow.textProperty().addListener(SearchPageUtils.textPropertyChangeListener(yearBelow, 4));
		ratingAbove.textProperty().addListener(SearchPageUtils.ratingChangeListener(ratingAbove));
		ratingBelow.textProperty().addListener(SearchPageUtils.ratingChangeListener(ratingBelow));
		
		chooseGenres.setCellFactory(listView  -> new GenreListCell());
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
		                // If filter text is empty, display all persons.
						if(DataUtils.isBlank(newValue))
		                    return true;
		                String lowerCaseFilter = newValue.toLowerCase();
		                if(g.toLowerCase().contains(lowerCaseFilter)) {
		                    return true;
		                }
		                return false;
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
        //initiate the movie result pane
        AppUtils.InitiatelistViewAsGridPage(movieResultPane);
	}
	
	private void removeGenreFromSelected(String genre) {
		genresSelected.remove(genre);
		chooseGenres.getEditor().setText("");
		Platform.runLater(() -> genresList.setPredicate(new Predicate<String>() {
			@Override
			public boolean test(String g) {
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
		List<MovieReference> searchList = SearchUtils.searchMoviesSort(sortDto);
		AppUtils.UpdatelistViewAsGridPage(movieResultPane, searchList);
	}
	
	@FXML
	private void searchMovies() {
		SortDto sortDto = new SortDto();
		sortDto.setName(nameField.getText());
		List<String> genresNames = new ArrayList<>();
		for(String genre : genresSelected)
			genresNames.add(genre);
		sortDto.setGenres(genresNames);
		sortDto.setYearAbove(DataUtils.getNumber(yearAbove.getText()));
		sortDto.setYearBelow(DataUtils.getNumber(yearBelow.getText()));
		sortDto.setRatingAbove(DataUtils.getNumber(ratingAbove.getText()));
		sortDto.setRatingBelow(DataUtils.getNumber(ratingBelow.getText()));
		searchMovies(sortDto);
	}
	
	private static class GenreListCell extends ListCell<String> {

	    @Override
	    public void updateItem(String item, boolean empty) {
	        super.updateItem(item, empty);
	        if(item == null || empty)
	            setText(null);
	        else
	        	setText(item);
	    }

	}
	
	private class SelectedGenreListCell extends ListCell<String> {

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
	    public void updateItem(String item, boolean empty) {
	        super.updateItem(item, empty);
	        if(item == null || empty) {
	        	setGraphic(null);
	            remove.setOnAction(null);
	            name.setText("");
	        }
	        else {
	        	name.setText(item);
	        	remove.setOnAction(e -> {
	        		removeGenreFromSelected(item);
	        	});
	        	setGraphic(pane);
	        }
	    }
	}
	
}
