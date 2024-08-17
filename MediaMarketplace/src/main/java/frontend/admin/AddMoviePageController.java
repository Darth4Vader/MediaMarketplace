package frontend.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.controllers.UserAuthenticateController;
import backend.dtos.CreateMovieDto;
import backend.exceptions.EntityNotFoundException;
import backend.tmdb.CanUpdateException;
import backend.tmdb.CreateMovie;
import backend.tmdb.CreateMovieException;
import backend.tmdb.MovieDtoSearchResult;
import frontend.admin.createMovieLogView.CreateMovieLoggerControl;
import frontend.utils.AppUtils;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Controller for the "Add Movie" page in the admin interface.
 * <p>This class manages the UI components and interactions for adding and updating movies in the application. It includes 
 * functionality for searching movies, displaying search results in a paginated view, and handling movie creation or 
 * update operations.</p>
 * <p>The controller is responsible for checking user permissions, handling search queries, managing pagination, and 
 * providing feedback through custom logging and alerts.</p>
 * 
 * @see CreateMovie
 * @see UserAuthenticateController
 * @see CreateMovieDto
 * @see MovieDtoSearchResult
 * @see CreateMovieLoggerControl
 * @see AppUtils
 */
@Component
public class AddMoviePageController {
	
	/**
	 * Path to the FXML file associated with this controller.
	 */
	public static final String PATH = "/frontend/admin/AddMoviePage.fxml";
	
	/**
	 * The main container for the Add Movie page UI components.
	 */
	@FXML
	private HBox mainPane;
	
	/**
	 * ListView component for displaying the search results of movies.
	 */
	@FXML
	ListView<CreateMovieDto> movieListView;
	
	/**
	 * TextField for entering search queries to find movies.
	 */
	@FXML
	private TextField searchField;
	
	/**
	 * Label that shows the current page number in the pagination controls.
	 */
	@FXML
	private Label pageLabel;
	
	/**
	 * Label for navigating to the previous page of search results.
	 */
	@FXML
	private Label previousPageLbl;
	
	/**
	 * Label for navigating to the next page of search results.
	 */
	@FXML
	private Label nextPageLbl;
	
	/**
	 * Label that is displayed when no search results are found.
	 */
	@FXML
	private Label noResultFoundLabel;
	
	/**
	 * Service for handling movie creation and updates in the backend.
	 */
	@Autowired
	private CreateMovie createMovie;
	
	/**
	 * Controller for managing user authentication and authorization.
	 */
	@Autowired
	private UserAuthenticateController userAuthenticateController;
	
	/**
	 * The current search result object containing information about the movies and pagination.
	 */
	private MovieDtoSearchResult movieDtoSearchResult;
	
	/**
	 * Observable list that backs the ListView with the movies to be displayed.
	 */
	private ObservableList<CreateMovieDto> movieList;
	
	/**
	 * Control for logging the progress of movie creation and updates.
	 */
	private CreateMovieLoggerControl createMovieLoggerControl;
	
	/**
	 * Initializes the controller by setting up UI bindings and checking user permissions.
	 * <p>This method is called automatically after the FXML file is loaded. It ensures that the current user has admin
	 * rights to access this page, sets up the initial state of the UI components, and binds properties.</p>
	 * <p>It also configures the ListView for displaying search results and sets up the custom logging control for 
	 * displaying progress.</p>
	 */
	@FXML
	private void initialize() {
		// first check that the current user is an admin to enter this page
		userAuthenticateController.checkIfCurrentUserIsAdmin();
		this.movieList = FXCollections.observableArrayList();
		movieListView.prefWidthProperty().bind(mainPane.widthProperty().multiply(0.6));
		previousPageLbl.setVisible(false);
		nextPageLbl.setVisible(false);
		movieListView.setCellFactory(x -> new SearchMovieCell(this));
		movieListView.setItems(movieList);
		movieListView.setSelectionModel(null);
		noResultFoundLabel.visibleProperty().bind(Bindings.isEmpty(movieList));
		
		createMovieLoggerControl = new CreateMovieLoggerControl(createMovie);
	}
	
	/**
	 * Updates the visibility of pagination controls based on the current search result.
	 * <p>This method adjusts the visibility of the previous and next page labels depending on the current page number
	 * and total number of pages in the search results. It also updates the page label to show the current page number.</p>
	 */
	private void validatePageMoveLabels() {
		if (movieDtoSearchResult != null) {
			int page = movieDtoSearchResult.getCurrentPage();
			int total = movieDtoSearchResult.getTotalPages();
			previousPageLbl.setVisible(page != 1);
			nextPageLbl.setVisible(page != total);
			pageLabel.setText("" + page);
		}
	}
	
	/**
	 * Performs a search for movies based on the text entered in the search field.
	 * <p>This method initiates a search operation using the text from the search field, updates the pagination labels,
	 * and displays the search results in the ListView. If no search text is provided, it exits early.</p>
	 */
	@FXML
	private void searchMovies() {
		String search = searchField.getText();
		if (search.isEmpty()) return;
		movieDtoSearchResult = createMovie.searchMovie(search);
		validatePageMoveLabels();
		List<CreateMovieDto> movies = movieDtoSearchResult.getResultList();
		viewMoviesSearched(movies);
	}
	
	/**
	 * Adds a movie to the database and manages the logging and UI updates.
	 * <p>This method creates a background task to add the movie to the database. It updates the UI to reflect the progress,
	 * handles exceptions that may occur during the process, and provides feedback to the user through alerts and logging.</p>
	 * 
	 * @param movie The movie to be added to the database.
	 */
	public void addMovieToDatabase(CreateMovieDto movie) {
		// first we will create a task in order to push the custom logger message to ListView by updating JavaFX
		Task<Void> task = new Task<Void>() {
			
			@Override
			protected Void call() throws Exception {
				createMovie.addMovieToDatabase(movie);
				return null;
			}
		};
		task.setOnFailed(v -> {
			// we will handle every possible exception of the method that we call
			createMovieLoggerControl.close();
			Throwable exception = task.getException();
			if (exception instanceof NumberFormatException) {
				AdminPagesUtils.parseNumberException(movie.getMediaID());
			} else if (exception instanceof CreateMovieException) {
				// when the movie creation fails, we will alert the user of the reasons
				AdminPagesUtils.createMovieExceptionAlert((CreateMovieException) exception);
			} else if (exception instanceof CanUpdateException) {
				// if the movie already exists, then we will alert the user that he can update the movie if he wants
				showAlertThatMovieCanUpdate((CanUpdateException) exception);
			} else { // and if the exception is not specified in the method, like RuntimeException, then we will let the default handler to handle it
				Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), exception);
			}
		});
		// we will add a successful notify id the method finish
		task.setOnSucceeded(v -> {
			createMovieLoggerControl.finishedTask();
		});
		// if the task is cancelled, before the method finish, then we notify the user of the consequences
		task.setOnCancelled(v -> {
			createMovieLoggerControl.close();
			AdminPagesUtils.cancelAddMovieAlert(movie);
		});
		// we will open the custom view with the task
		createMovieLoggerControl.start(task);
		// and then start the task as a new thread, in order to synchronize with JavaFX
		new Thread(task).start();
	}
	
	/**
	 * Updates an existing movie in the database and handles any exceptions that may occur.
	 * <p>This method creates a background task to update the movie in the database. It handles exceptions that occur during
	 * the update process and provides feedback to the user through alerts and logging. It also manages the UI updates based 
	 * on the result of the task.</p>
	 * 
	 * @param e Exception thrown when a movie can be updated.
	 */
	private void updateMovieInDatabase(CanUpdateException e) {
		// first we will create a task in order to push the custom logger message to ListView by updating JavaFX
		Task<Void> task = new Task<Void>() {
			
			@Override
			protected Void call() throws Exception {
				createMovie.updateMovieInDatabase(e);
				return null;
			}
		};
		task.setOnFailed(v -> {
			// we will handle every possible exception of the method that we call
			createMovieLoggerControl.close();
			Throwable exception = task.getException();
			if (exception instanceof CreateMovieException) {
				createMovieLoggerControl.close();
				// when the movie creation fails, we will alert the user of the reasons
				AdminPagesUtils.createMovieExceptionAlert((CreateMovieException) exception);
			} else if (exception instanceof EntityNotFoundException) {
				createMovieLoggerControl.close();
				// can ignore, because we create the genres, so the exception will not be triggered.
				// and the movie is created, therefore it will be in the database, and the exception will not be triggered
				AppUtils.alertOfError("Update Movie Error", exception.getMessage());
			} else { // and if the exception is not specified in the method, like RuntimeException, then we will let the default handler to handle it
				Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), exception);
			}
		});
		// we will add a successful notify id the method finish
		task.setOnSucceeded(v -> {
			createMovieLoggerControl.finishedTask();
		});
		// if the task is cancelled, before the method finish, then we notify the user of the consequences
		task.setOnCancelled(v -> {
			createMovieLoggerControl.close();
			AdminPagesUtils.cancelUpdateMovieAlert(e);
		});
		// we will open the custom view with the task
		createMovieLoggerControl.start(task);
		// and then start the task as a new thread, in order to synchronize with JavaFX
		new Thread(task).start();
	}
	
	/**
	 * Shows an alert indicating that a movie can be updated if it already exists.
	 * <p>This method displays an information alert to the user if the movie being added already exists in the database.
	 * The alert provides an option to update the existing movie instead of creating a new entry.</p>
	 * 
	 * @param e The exception that indicates the movie can be updated.
	 */
	private void showAlertThatMovieCanUpdate(CanUpdateException e) {
		// if the movie already exists, then we will alert the user that he can update the movie if he wants
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
        alert.getDialogPane().setContent(box);
        alert.show();
	}
	
	/**
	 * Updates the ListView with the searched movies.
	 * <p>This method sets the items of the ListView to the provided list of movies, which are the result of a search operation.</p>
	 * 
	 * @param movies The list of movies to be displayed in the ListView.
	 */
	private void viewMoviesSearched(List<CreateMovieDto> movies) {
		movieList.setAll(movies);
	}
	
	/**
	 * Handles the action to move to the previous page of search results.
	 * <p>This method is invoked when the user clicks the previous page button. It adjusts the current page number and
	 * updates the displayed search results accordingly.</p>
	 */
	@FXML
	private void previousPage() {
		movePage(false);
	}
	
	/**
	 * Handles the action to move to the next page of search results.
	 * <p>This method is invoked when the user clicks the next page button. It adjusts the current page number and updates
	 * the displayed search results accordingly.</p>
	 */
	@FXML
	private void nextPage() {
		movePage(true);
	}
	
	/**
	 * Moves the pagination to the next or previous page based on the provided direction.
	 * <p>This method updates the current page number and retrieves the corresponding page of search results. It also updates
	 * the pagination labels and the ListView to reflect the new page.</p>
	 * 
	 * @param forward Indicates whether to move to the next page (true) or the previous page (false).
	 */
	private void movePage(boolean forward) {
		if (movieDtoSearchResult != null) {
			int page = movieDtoSearchResult.getCurrentPage();
			int total = movieDtoSearchResult.getTotalPages();
			if (forward)
				page++;
			else
				page--;
			if (page >= 0 && page <= total) {
				String search = movieDtoSearchResult.getSearchText();
				movieDtoSearchResult = createMovie.searchMovie(search, page);
				validatePageMoveLabels();
				List<CreateMovieDto> movies = movieDtoSearchResult.getResultList();
				viewMoviesSearched(movies);
			}
		}
	}
}