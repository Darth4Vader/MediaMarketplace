package frontend.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.controllers.UserAuthenticateController;
import backend.dtos.CreateMovieDto;
import backend.dtos.MovieDto;
import backend.exceptions.EntityNotFoundException;
import backend.tmdb.CanUpdateException;
import backend.tmdb.CreateMovie;
import backend.tmdb.CreateMovieException;
import backend.tmdb.MovieDtoSearchResult;
import frontend.admin.createMovieLogView.CreateMovieLoggerControl;
import frontend.utils.AppUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

@Component
public class AddMoviePageController {
	
	public static final String PATH = "/frontend/admin/AddMoviePage.fxml";
	
	@FXML
	private HBox mainPane;
	
	@FXML
	ListView<CreateMovieDto> movieListView;
	
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
	
	@Autowired
	private UserAuthenticateController userAuthenticateController;
	
	private MovieDtoSearchResult movieDtoSearchResult;
	
	private ObservableList<CreateMovieDto> movieList;
	
	private CreateMovieLoggerControl createMovieLoggerControl;
	
	@FXML
	private void initialize() {
		//first check that the current user is an admin to enter this page
		userAuthenticateController.checkIfCurrentUserIsAdmin();
		this.movieList = FXCollections.observableArrayList();
		movieListView.prefWidthProperty().bind(mainPane.widthProperty().multiply(0.6));
		previousPageLbl.setVisible(false);
		nextPageLbl.setVisible(false);
		movieListView.setCellFactory(x ->  new SearchMovieCell(this));
		movieListView.setItems(movieList);
		movieListView.setSelectionModel(null);
		
		createMovieLoggerControl = new CreateMovieLoggerControl(createMovie);
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
		List<CreateMovieDto> movies = movieDtoSearchResult.getResultList();
		viewMoviesSearched(movies);
	}
	
	public void addMovieToDatabase(CreateMovieDto movie) {
		createMovieLoggerControl.start();
		try {
			createMovie.addMovieToDatabase(movie);
			createMovieLoggerControl.finishedTask();
		} catch (NumberFormatException e1) {
			AdminPagesUtils.parseNumberException(movie.getMediaID());
		} catch (CreateMovieException e) {
			createMovieLoggerControl.close();
			//when the movie creation fails, we will alert the user of the reasons
			AdminPagesUtils.createMovieExceptionAlert(e);
		} catch (CanUpdateException e) {
			createMovieLoggerControl.close();
			//if the movie already exists, then we will  alert the user that he can update the movie if he wants
			Alert alert = AppUtils.createAlertOfInformation("Movie Creation exception", e.getMessage());
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
	        alert.show();
		}
	}
	
	private void updateMovieInDatabase(CanUpdateException e) {
		createMovieLoggerControl.start();
		try {
			createMovie.updateMovieInDatabase(e);
			createMovieLoggerControl.finishedTask();
		} catch (CreateMovieException e1) {
			createMovieLoggerControl.close();
			//when the movie creation fails, we will alert the user of the reasons
			AdminPagesUtils.createMovieExceptionAlert(e1);
		} catch (EntityNotFoundException e1) {
			createMovieLoggerControl.close();
			//can ignore, because we create the genres, so the exception will not be triggered.
			//and the movie is created, therefore it will be in the database, and the exception will not be triggered
			AppUtils.alertOfError("Update Movie Error", e.getMessage());
		}
	}
	
	private void viewMoviesSearched(List<CreateMovieDto> movies) {
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
				List<CreateMovieDto> movies = movieDtoSearchResult.getResultList();
				viewMoviesSearched(movies);
			}
		}
	}
}
