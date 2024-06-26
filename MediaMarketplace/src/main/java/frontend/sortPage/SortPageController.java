package frontend.sortPage;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.DataUtils;
import backend.controllers.GenreController;
import backend.entities.Genre;
import backend.entities.Movie;
import frontend.AppUtils;
import frontend.SearchUtils;
import frontend.SortDto;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TitledPane;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

@Component
public class SortPageController {
	
	public static final String PATH = "/frontend/sortPage/SortPage.fxml";
	
	@FXML private TextField nameField;
	
	@FXML
	private ComboBox<Genre> chooseGenres;
	
	@FXML
	private GridPane genresPane;
	
	@FXML
	private TilePane resultPane;
	
	@FXML
	private AnchorPane mainPane;
	
	@FXML private TextField yearUp;
	@FXML private TextField yearDown;
	@FXML private TextField ratingUp;
	@FXML private TextField ratingDown;
	
	@Autowired
	private GenreController genreController;
	
    /*private static <T> ListView<T> getListView(ComboBox<T> cb) {
    	ComboBoxListViewSkin<T> skin = (ComboBoxListViewSkin<T>) cb.getSkin();
    	ListView<T> list = (ListView<T>) skin.getPopupContent();
    	return list;
    }*/
	
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
	
	/*public class DecimalFormatter extends StringConverter {

	    private final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.ITALY);

	    public DecimalFormatter(int fractionsLenght) {
	        numberFormat.setMaximumFractionDigits(fractionsLenght);
	        numberFormat.setMinimumFractionDigits(fractionsLenght);
	    }

	    public DecimalFormatter() {
	        numberFormat.setMaximumFractionDigits(2);
	        numberFormat.setMinimumFractionDigits(2);
	    }

	    @Override
	    public String toString(BigDecimal value) {
	        if ( value == null )
	            value = new BigDecimal(0);
	        return numberFormat.format(value.doubleValue());
	    }

	    @Override
	    public Decimal fromString(String value) {
	        value = value.replaceAll(",", ".");
	        return new BigDecimal(value);
	    }

		@Override
		public String toString(Object object) {
			// TODO Auto-generated method stub
			return null;
		}
	}*/
	
	private ObservableList<Genre> genresSelected;
	
	@FXML
	private void initialize() {
		genresSelected = FXCollections.observableArrayList();
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
		/*chooseGenres.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.UP) {
	            ListView<Genre> listView = getListView(chooseGenres);
	            int selectedIndex = listView.getSelectionModel().getSelectedIndex();
	            listView.scrollTo(selectedIndex == 0 ? selectedIndex : selectedIndex + 1);
	        }
			else if(e.getCode() == KeyCode.DOWN) {
	            ListView<Genre> listView = getListView(chooseGenres);
	            int selectedIndex = listView.getSelectionModel().getSelectedIndex();
	            listView.scrollTo(selectedIndex == 0 ? selectedIndex : selectedIndex - 1);
			}
		});*/
		List<Genre> genres = genreController.getAllGenres();
		ObservableList<Genre> list = FXCollections.observableArrayList(genres);
		FilteredList<Genre> filteredData = new FilteredList<>(list);
		chooseGenres.getEditor().textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				Platform.runLater(() -> filteredData.setPredicate(new Predicate<Genre>() {

					@Override
					public boolean test(Genre g) {
						System.out.println(genresSelected);
						System.out.println(genresSelected.contains(g));
						if(genresSelected.contains(g))
							return false;
		                // If filter text is empty, display all persons.
						if(DataUtils.isBlank(newValue))
		                    return true;
		                String lowerCaseFilter = newValue.toLowerCase();
		                //StringProperty property = t.getNameProperty(); 
		                if(g.getName().toLowerCase().contains(lowerCaseFilter)) {
		                    return true;
		                }
		                return false;
					}
	            }));
			}
		});
		/*chooseGenres.showingProperty().addListener((obs, wasShowing, isNowShowing) -> {
            if (isNowShowing) {
            	filteredData.setPredicate(genrePredicate(genresSelected, ""));
            }
        });*/
	    chooseGenres.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Genre>() {

			@Override
			public void changed(ObservableValue<? extends Genre> observable, Genre oldValue, Genre newValue) {
				if(newValue != null) {
					BorderPane pane = new BorderPane();
					Button remove = new Button("x");
					remove.setTextFill(Color.RED);
					remove.setOnAction(e -> {
						genresSelected.remove(newValue);
						genresPane.getChildren().remove(pane);
						chooseGenres.getEditor().setText("");
						Platform.runLater(() -> filteredData.setPredicate(new Predicate<Genre>() {
							@Override
							public boolean test(Genre g) {
								if(genresSelected.contains(g))
									return false;
				                return true;
							}
			            }));
					});
					pane.setLeft(remove);
					Label genre = new Label(newValue.getName());
					pane.setCenter(genre);
					genresSelected.add(newValue);
					int size = genresSelected.size(); 
					int r = size / 3;
					int c = size - r*3;
					genresPane.add(pane, c, r);
					chooseGenres.getEditor().setText("");
				}
				
				System.out.println(newValue);
			}
	      });
		/*
        // 3. Wrap the FilteredList in a SortedList. 
        SortedList<Genre> sortedData = new SortedList<>(filteredData);
        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(chooseGenres.comparatorProperty());
        // 5. Add sorted (and filtered) data to the table.
        */
        chooseGenres.setItems(filteredData);
        //Platform.runLater(() -> chooseGenres.getSelectionModel().select(0));
	}
	
	private static class GenreListCell extends ListCell<Genre> {

	    @Override
	    public void updateItem(Genre item, boolean empty) {
	        super.updateItem(item, empty);
	        if (item != null) {
	            setText(item.getName());//return String, actual name of material
	        }
	        else {
	            setText(null);
	        }
	    }

	}
	
	@FXML
	private void searchMovies() {
		resultPane.getChildren().clear();
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
		List<Movie> searchList = SearchUtils.searchMoviesSort(sortDto);
		for(Movie movie : searchList)
			try {
				//Pane pane = AppUtils.getMoviePane(movie);
				ImageView view = AppUtils.loadImageViewFromClass(movie.getPosterPath());
				view.setPreserveRatio(true);
				BorderPane b = new BorderPane();
				view.fitWidthProperty().bind(mainPane.widthProperty());
				view.fitHeightProperty().bind(mainPane.heightProperty().multiply(0.2));
				b.setCenter(view);
				Label name = new Label(movie.getName());
				b.setBottom(name);
				b.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
			            new BorderWidths(1))));
				//pane.prefWidthProperty().bind(((Pane) resultPane.getParent()).widthProperty().multiply(0.2));
				//pane.prefHeightProperty().bind(((Pane) resultPane.getParent()).heightProperty().multiply(0.2));
				resultPane.getChildren().add(b);
				//AppUtils.addToGridPane(resultPane, AppUtils.getMoviePane(movie));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
}
