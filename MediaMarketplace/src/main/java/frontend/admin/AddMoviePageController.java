package frontend.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.dto.mediaProduct.MovieDto;
import backend.exceptions.EntityNotFoundException;
import backend.tmdb.CanUpdateException;
import backend.tmdb.CreateMovie;
import backend.tmdb.CreateMovieException;
import backend.tmdb.MovieDtoSearchResult;
import backend.tmdb.NameAndException;
import frontend.AppUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

@Component
public class AddMoviePageController {
	
	public static final String PATH = "/frontend/admin/AddMoviePage.fxml";
	
	@FXML
	private HBox mainPane;
	
	@FXML
	ListView<MovieDto> movieListView;
	
	@FXML
	private TextField searchField;
	
	@FXML
	private Label pageLabel;
	
	@FXML
	private Label previousPageLbl;
	
	@FXML
	private Label nextPageLbl;
	
	@Autowired
	private CreateMovie createMovie;
	
	private MovieDtoSearchResult movieDtoSearchResult;
	
	private ObservableList<MovieDto> movieList;
	
	@FXML
	private void initialize() {
		this.movieList = FXCollections.observableArrayList();
		movieListView.prefWidthProperty().bind(mainPane.widthProperty().multiply(0.6));
		previousPageLbl.setVisible(false);
		nextPageLbl.setVisible(false);
		movieListView.setCellFactory(x ->  new SearchMovieCell(this));
		movieListView.setItems(movieList);
		movieListView.setSelectionModel(null);
	}
	
	private void validatePageMoveLabels() {
		if(movieDtoSearchResult != null) {
			int page = movieDtoSearchResult.getCurrentPage();
			int total = movieDtoSearchResult.getTotalPages();
			previousPageLbl.setVisible(page != 1);
			nextPageLbl.setVisible(page != total);
			pageLabel.setText(""+page);
		}
	}
	
	@FXML
	private void searchMovies() {
		String search = searchField.getText();
		if(search.isEmpty()) return;
		movieDtoSearchResult = createMovie.searchMovie(search);
		validatePageMoveLabels();
		List<MovieDto> movies = movieDtoSearchResult.getResultList();
		viewMoviesSearched(movies);
	}
	
	public void addMovieToDatabase(MovieDto movie){
		String mediaId = movie.getMediaID();
		try {
			createMovie.addMovieToDatabase(
					Integer.parseInt(mediaId));
		} catch (NumberFormatException e1) {
			//this can happen when the media id is not a number
			//we will add an error message
			AppUtils.alertOfError("Movie Creation failed", "The movie media id (" + mediaId + ") is not a number");
		} catch (CreateMovieException e) {
			//when the movie creation fails, we will alert the user of the reasons
			createMovieExceptionAlert(e);
		} catch (CanUpdateException e) {
			//if the movie already exists, then we will  alert the user that he can update the movie if he wants
			Alert alert = AppUtils.alertOfInformation("Movie Creation exception", e.getMessage());
			VBox box = new VBox();
			Label description = new Label("The movie can be updated if the update button is clicked");
			Button btn = new Button("Update the movie");
			btn.setOnAction(evt -> {
        		alert.close();
        		updateMovieInDatabase(e);
	        });
	        btn.setStyle("-fx-font-weight: bold;");
	        box.getChildren().addAll(description, btn);
	        alert.getDialogPane().setGraphic(box);
		}
	}
	
	private void updateMovieInDatabase(CanUpdateException e) {
		try {
			createMovie.updateMovieInDatabase(e);
		} catch (CreateMovieException e1) {
			//when the movie creation fails, we will alert the user of the reasons
			createMovieExceptionAlert(e1);
		} catch (EntityNotFoundException e1) {
			//can ignore, because we create the genres, so the exception will not be triggered.
			//and the movie is created, therefore it will be in the database, and the exception will not be triggered
		}
	}
	
	private void createMovieExceptionAlert(CreateMovieException e) {
		Alert alert = AppUtils.alertOfError("Movie Creation exception", e.getMessage());
		VBox box = new VBox();
		box.setSpacing(5);
		List<NameAndException> list = e.getList();
		if(list != null) for(NameAndException nameAndException : list) {
			HBox eBox = new HBox();
			eBox.setSpacing(10);
			Label nameLbl = new Label(nameAndException.getName());
			nameLbl.setStyle("-fx-font-weight: bold;");
			Label causeLbl = new Label(nameAndException.getException().getMessage());
			causeLbl.setTextFill(Color.RED);
			eBox.getChildren().addAll(nameLbl, causeLbl);
			box.getChildren().add(eBox);
		}
		ScrollPane pane = new ScrollPane(box);
        alert.getDialogPane().setGraphic(pane);
	}
	
	private void viewMoviesSearched(List<MovieDto> movies) {
		movieList.setAll(movies);
	}
	
	@FXML
	private void previousPage() {
		movePage(false);
	}
	
	@FXML
	private void nextPage() {
		movePage(true);
	}
	
	private void movePage(boolean forward) {
		if(movieDtoSearchResult != null) {
			int page = movieDtoSearchResult.getCurrentPage();
			int total = movieDtoSearchResult.getTotalPages();
			if(forward)
				page++;
			else
				page--;
			if(page >= 0 && page <= total) {
				String search = movieDtoSearchResult.getSearchText();
				movieDtoSearchResult = createMovie.searchMovie(search, page);
				validatePageMoveLabels();
				List<MovieDto> movies = movieDtoSearchResult.getResultList();
				viewMoviesSearched(movies);
			}
		}
	}
}
