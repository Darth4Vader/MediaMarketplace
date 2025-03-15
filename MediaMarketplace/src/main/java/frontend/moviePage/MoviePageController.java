package frontend.moviePage;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.DataUtils;
import backend.controllers.ActorController;
import backend.controllers.CartController;
import backend.controllers.DirectorController;
import backend.controllers.MovieController;
import backend.controllers.MoviePurchasedController;
import backend.controllers.MovieReviewController;
import backend.controllers.ProductController;
import backend.controllers.UserAuthenticateController;
import backend.dtos.ActorDto;
import backend.dtos.DirectorDto;
import backend.dtos.MovieDto;
import backend.dtos.MoviePurchasedDto;
import backend.dtos.MovieReviewDto;
import backend.dtos.ProductDto;
import backend.dtos.references.CartProductReference;
import backend.dtos.references.MovieReference;
import backend.dtos.references.MovieReviewReference;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.UserNotLoggedInException;
import frontend.App;
import frontend.AppImageUtils;
import frontend.utils.AppUtils;
import frontend.utils.UserChangeInterface;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

@Component
public class MoviePageController implements UserChangeInterface {
	
	/**
	 * The path to the FXML file for the movie page layout.
	 * <p>This path is used to load the MoviePage.fxml file which defines the user interface for the movie page.</p>
	 */
	public static final String PATH = "/frontend/moviePage/MoviePage.fxml";
	
	/**
	 * The main container for the movie page, allowing for scrolling.
	 * <p>This {@link ScrollPane} holds the primary content of the movie page and supports scrolling for overflow content.</p>
	 */
	@FXML
	private ScrollPane mainPane;
	
	/**
	 * The border pane used to display the background image of the movie.
	 * <p>This {@link BorderPane} is responsible for showing the movie's backdrop image.</p>
	 */
	@FXML
	private BorderPane backgroundView;
	
	/**
	 * The image view for displaying the movie's poster.
	 * <p>This {@link ImageView} shows the poster image of the movie.</p>
	 */
	@FXML
	private ImageView posterView;
	
	/**
	 * The VBox that contains product-related options for the movie.
	 * <p>This {@link VBox} includes buttons for purchasing or renting the movie.</p>
	 */
	@FXML
	private VBox productOptions;
	
	/**
	 * The text area that displays the movie's synopsis.
	 * <p>This {@link TextArea} shows a detailed description or summary of the movie.</p>
	 */
	@FXML
	private TextArea synopsisArea;
	
	/**
	 * The label that displays the name of the movie.
	 * <p>This {@link Label} shows the title of the movie.</p>
	 */
	@FXML
	private Label nameLbl;
	
	/**
	 * The text flow that contains the rating button and user rating information.
	 * <p>This {@link TextFlow} includes the UI components for displaying and interacting with movie ratings.</p>
	 */
	@FXML
	private TextFlow ratingButton;
	
	/**
	 * The label that displays the release year of the movie.
	 * <p>This {@link Label} shows the year in which the movie was released.</p>
	 */
	@FXML
	private Label yearLbl;
	
	/**
	 * The label that displays the runtime of the movie.
	 * <p>This {@link Label} shows the total duration of the movie in hours and minutes.</p>
	 */
	@FXML
	private Label rutimeLbl;
	
	/**
	 * The label that displays the number of ratings the movie has received.
	 * <p>This {@link Label} shows the total count of ratings for the movie.</p>
	 */
	@FXML
	private Label movieRatingNumberLbl;
	
	/**
	 * The HBox that contains options for watching the movie.
	 * <p>This {@link HBox} includes UI components related to movie viewing options, such as buttons for watching or renting.</p>
	 */
	@FXML
	private HBox watchOptions;
	
	/**
	 * The ListView that displays the list of directors associated with the movie.
	 * <p>This {@link ListView} shows the directors involved in the movie.</p>
	 */
	@FXML
	private ListView<DirectorDto> directorsListView;
	
	/**
	 * The ListView that displays the list of actors featured in the movie.
	 * <p>This {@link ListView} shows the actors who performed in the movie.</p>
	 */
	@FXML
	private ListView<ActorDto> actorsListView;
	
	/**
	 * The ListView that displays the list of reviews for the movie.
	 * <p>This {@link ListView} shows user reviews and ratings for the movie.</p>
	 */
	@FXML
	private ListView<MovieReviewDto> reviewsListView;
	
	/**
	 * Controller for managing movie-related operations.
	 * <p>This {@link MovieController} handles the retrieval and management of movie data.</p>
	 */
	@Autowired
	private MovieController movieController;
	
	/**
	 * Controller for managing product-related operations.
	 * <p>This {@link ProductController} handles product details, including buying and renting options.</p>
	 */
	@Autowired
	private ProductController productController;
	
	/**
	 * Controller for managing director-related operations.
	 * <p>This {@link DirectorController} handles retrieval of director information associated with movies.</p>
	 */
	@Autowired
	private DirectorController directorController;
	
	/**
	 * Controller for managing actor-related operations.
	 * <p>This {@link ActorController} handles retrieval of actor information associated with movies.</p>
	 */
	@Autowired
	private ActorController actorsController;
	
	/**
	 * Controller for managing movie purchase operations.
	 * <p>This {@link MoviePurchasedController} handles tracking of movie purchases and rentals by users.</p>
	 */
	@Autowired
	private MoviePurchasedController moviePurchasedController;
	
	/**
	 * Controller for managing shopping cart operations.
	 * <p>This {@link CartController} handles adding and managing products in the user's cart.</p>
	 */
	@Autowired
	private CartController cartController;
	
	/**
	 * Controller for managing movie reviews.
	 * <p>This {@link MovieReviewController} handles adding and retrieving movie reviews from users.</p>
	 */
	@Autowired
	MovieReviewController movieReviewController;
	
    /**
     * Controller for user authentication operations.
     */
	@Autowired
	private UserAuthenticateController userAuthenticateController;
	
	/**
	 * The current movie being displayed.
	 * <p>This {@link MovieDto} object holds the details of the movie currently shown in the UI.</p>
	 */
	MovieDto movie;
	
	/**
	 * Timeline for managing the countdown of remaining rental time.
	 * <p>This {@link Timeline} is used to update and display the remaining time for movie rentals.</p>
	 */
	private Timeline remainingRentTime;
	
	/**
	 * List of directors associated with the current movie.
	 * <p>This {@link ObservableList} contains {@link DirectorDto} objects representing the directors of the movie.</p>
	 */
	private ObservableList<DirectorDto> directorsList;
	
	/**
	 * List of actors featured in the current movie.
	 * <p>This {@link ObservableList} contains {@link ActorDto} objects representing the actors of the movie.</p>
	 */
	private ObservableList<ActorDto> actorsList;
	
	/**
	 * List of reviews for the current movie.
	 * <p>This {@link ObservableList} contains {@link MovieReviewDto} objects representing user reviews of the movie.</p>
	 */
	private ObservableList<MovieReviewDto> movieReviewsList;
	
	/**
	 * Flag indicating whether this is the user's first time visiting the movie page.
	 * <p>This boolean value determines if the user is revisiting the movie page or accessing it for the first time.</p>
	 */
	private boolean notFirstTimeInsidePage;
	
	/**
	 * Initializes the controller. Sets up the lists for directors, actors, and reviews,
	 * and configures their appearance and behavior.
	 */
	@FXML
	private void initialize() {
		this.notFirstTimeInsidePage = false;
		directorsList = FXCollections.observableArrayList();
		directorsListView.setCellFactory(x -> new PersonCell<DirectorDto>());
		directorsListView.setItems(directorsList);
		directorsListView.setSelectionModel(null);
		directorsListView.prefHeightProperty().bind(mainPane.heightProperty().multiply(0.3).add(60));
		
		actorsList = FXCollections.observableArrayList();
		actorsListView.setCellFactory(x ->  new PersonCell<ActorDto>());
		actorsListView.setItems(actorsList);
		actorsListView.setSelectionModel(null);
		actorsListView.prefHeightProperty().bind(mainPane.heightProperty().multiply(0.3).add(60));
		
		movieReviewsList = FXCollections.observableArrayList();
		reviewsListView.setCellFactory(x -> new MovieReviewCell());
		reviewsListView.setItems(movieReviewsList);
		reviewsListView.setSelectionModel(null);
		reviewsListView.prefHeightProperty().bind(mainPane.heightProperty().multiply(0.4));
	}
	
	/**
	 * <p>Initializes the movie details in the user interface based on the given {@link MovieReference} object.</p>
	 * 
	 * <p>This method retrieves the {@link MovieDto} for the movie identified by the ID in the provided {@link MovieReference}
	 * using the {@link movieController}. It then invokes the {@link #initializeMovie(MovieDto)} method to display the movie
	 * details in the user interface.</p>
	 * 
	 * <p>If the movie cannot be found:</p>
	 * <ul>
	 *     <li>Handles the {@link EntityNotFoundException} by showing an error alert to the user with the message from the exception,
	 *         indicating that there was an issue loading the movie details.</li>
	 * </ul>
	 * 
	 * @param movieReference The {@link MovieReference} object containing the ID of the movie to retrieve and initialize.
	 * 
	 */
	public void initializeMovie(MovieReference movieReference) {
		try {
			MovieDto movie = movieController.getMovie(movieReference.getId());
			initializeMovie(movie);
		} catch (EntityNotFoundException e) {
			//if there is a problem with loading the movie page, then we will will notify the user
			AppUtils.alertOfError("Loading Movie Error", e.getMessage());
		}
	}
	
	/**
	 * <p>Initializes the movie details in the user interface based on the provided {@link MovieDto} object.</p>
	 * 
	 * <p>This method updates the UI elements to reflect the details of the specified movie, including:</p>
	 * <ul>
	 *     <li>Setting the movie's backdrop image, poster, name, synopsis, year, and runtime.</li>
	 *     <li>Displaying purchase and rental options via the {@link #checkMovieButtons()} method.</li>
	 *     <li>Loading and displaying user-specific ratings and reviews if available.</li>
	 *     <li>Retrieving and displaying lists of directors and actors associated with the movie.</li>
	 * </ul>
	 * 
	 * <p>The method handles cases where data may not be available or where the user is not logged in:</p>
	 * <ul>
	 *     <li>If the user has not rated the movie or is not logged in, it shows a default "Rate movie" prompt.</li>
	 *     <li>If movie-related data such as directors, actors, or reviews cannot be fetched, it handles exceptions gracefully
	 *         without interrupting the UI updates.</li>
	 * </ul>
	 * 
	 * @param movie The {@link MovieDto} object containing movie details to be displayed.
	 */
	public void initializeMovie(MovieDto movie) {
		if(this.movie == movie)
			this.notFirstTimeInsidePage = true;
		else
			this.notFirstTimeInsidePage = false;
		this.movie = movie;
		refreshAllUserInformationInPane();
		String backdropPath = movie.getBackdropPath(); 
		if(DataUtils.isNotBlank(backdropPath)) {
			Image backgroundImage = AppImageUtils.loadImageFromClass(movie.getBackdropPath());
			if(backgroundImage != null) {
				BackgroundSize backgroundSize = new BackgroundSize(1,
						1,
				        true,
				        true,
				        false,
				        false);
				BackgroundImage backgroudImage = new BackgroundImage(backgroundImage,
				        BackgroundRepeat.NO_REPEAT,
				        BackgroundRepeat.NO_REPEAT,
				        BackgroundPosition.CENTER,
				        backgroundSize);
				backgroundView.setBackground(new Background(backgroudImage));
			}
		}
		posterView.setImage(AppImageUtils.loadImageFromClass(movie.getPosterPath()));
		nameLbl.setText(movie.getName());
		String synopsis = movie.getSynopsis();
		if(DataUtils.isBlank(synopsis))
			synopsis = "To Be Determined";
		synopsisArea.setText(movie.getSynopsis());
		Integer year = movie.getYear();
		if(year != null)
			yearLbl.setText(""+year);
		Integer runtime = movie.getRuntime();
		if(runtime != null) {
			int hours = runtime / 60;
			int minutes = runtime - hours*60;
			rutimeLbl.setText(""+hours+"h "+minutes+"m");			
		}
		Long movieId = movie.getId();
		try {
			directorsList.clear();
			List<DirectorDto> directors = directorController.getDirectorsOfMovie(movieId);
			if(directors != null) {
				directorsList.setAll(directors);
			}
		}
		catch (EntityNotFoundException e) {
			//if there is a problem with loading the movie, and there shouldn't be one, then we won't load the directors
		}
		try {
			actorsList.clear();
			List<ActorDto> actors = actorsController.getActorsOfMovie(movieId);
			if(actors != null) {
				actorsList.setAll(actors);
			}
		}
		catch (EntityNotFoundException e) {
			//if there is a problem with loading the movie, and there shouldn't be one, then we won't load the actors
		}
		try {
			movieReviewsList.clear();
			List<MovieReviewDto> reviews = movieReviewController.getAllReviewOfMovie(movie.getId());
			if(reviews != null && !reviews.isEmpty()) {
				Integer ratingNumber = movieReviewController.getMovieRatings(movie.getId());
				if(ratingNumber != null)
					movieRatingNumberLbl.setText(""+ratingNumber);
				for(MovieReviewDto review : reviews) {
					MovieReviewReference movieReviewReference = review.getMovieReview();
					if(movieReviewReference != null && DataUtils.isNotBlank(movieReviewReference.getReviewTitle())) {
						movieReviewsList.add(review);	
					}
				}
			}
		} catch (EntityNotFoundException e) {
			//it's okay, a movie can be not reviewed
		}
	}
	

	@Override
	public void refreshAllUserInformationInPane() {
		checkWatchMovieButtons();
		setRatingsLabel();
	}
	
	/**
	 * Updates the movie options based on the user's admin status.
	 * <p>If the user is an admin, a "Watch Movie" button is added to the options.
	 * Otherwise, purchase buttons are added. The remaining rental time is stopped 
	 * and existing options are cleared before updating.</p>
	 * <p>If there's an issue checking the user's admin status, the method assumes 
	 * the user is not an admin.</p>
	 */
	private void checkWatchMovieButtons() {
		if(remainingRentTime != null)
			remainingRentTime.stop();
		productOptions.getChildren().clear();
		// We check if the user is an admin, and if so, he can watch every movie without needing to purchase it
		boolean isAdmin = false;
		try {
			isAdmin = userAuthenticateController.isCurrentUserAdmin();
		}
		catch (Throwable e) {
			// If the user is not logged, then he defiantly not an admin. 
		}
		if(isAdmin) {
			TextFlow watchMovieButton = new TextFlow();
			watchMovieButton.getChildren().add(new Text("Watch Movie"));
			setWatchMovieButton(watchMovieButton);
			productOptions.getChildren().add(watchMovieButton);
		}
		else {
			// Add purchase buttons for users that are not admins
			addPurchaseButtons();
		}
	}
	
	/**
	 * <p>Adds purchase and rental buttons to the product options UI based on the current movie's purchase and rental status.</p>
	 * 
	 * <p>This method performs the following actions:</p>
	 * <ul>
	 *     <li>Checks if the movie has been purchased or rented by the user and updates UI elements accordingly.</li>
	 *     <li>If the movie is purchased or rented, it adds a "Watch Movie" button. If only rented, it also displays the remaining rental time.</li>
	 *     <li>If the movie is not purchased, it adds options to buy or rent the movie, with respective prices.</li>
	 * </ul>
	 */
	private void addPurchaseButtons() {
		boolean isPurchased = false;
		boolean isRented = false;
		if(remainingRentTime != null)
			remainingRentTime.stop();
		productOptions.getChildren().clear();
		try {
			LocalDateTime currentRentTime = null;
			List<MoviePurchasedDto> purchasedLists = moviePurchasedController.getActiveListUserMovie(movie.getId());
			for(MoviePurchasedDto purchased : purchasedLists) {
				if(!purchased.isRented()) {
					isPurchased = true;
					break;
				}
				else {
					isRented = true;
					currentRentTime = purchased.getRentTimeSincePurchase();
				}
			}
			if(isPurchased || isRented) {
				TextFlow watchMovieButton = new TextFlow();
				watchMovieButton.getChildren().add(new Text("Watch Movie"));
				if(!isPurchased && isRented) {
					Text remainTimeText = new Text();
					watchMovieButton.getChildren().addAll(new Text(" (Rent)\nRemaining Time: "), remainTimeText);
					createRentCountdown(currentRentTime, remainTimeText);
				}
				setWatchMovieButton(watchMovieButton);
				productOptions.getChildren().add(watchMovieButton);
			}
		} catch (EntityNotFoundException e) {
			//This is okay, no need to add exception handling, the product will not be owned by the user
			//it just means that the user never purchased the movie.
		}
		catch (UserNotLoggedInException e) {
			//Can be if the user is not logged in, let him watch, but he can't buy as a guest
		}
		if(!isPurchased) try {
			ProductDto product = productController.getProductOfMovie(movie.getId());
			Button buyBtn = new Button("Buy for: " + product.getFinalBuyPrice());
			buyBtn.setWrapText(true);
			buyBtn.setOnAction(e -> {
				addToCart(product, true);
			});
			buyBtn.setMaxWidth(Double.MAX_VALUE);
			productOptions.getChildren().add(buyBtn);
			if(!isRented) {
				Button rentBtn = new Button("Rent for: " + product.getFinalRentPrice());
				rentBtn.setWrapText(true);
				rentBtn.setOnAction(e -> {
					addToCart(product, false);
				});
				rentBtn.setMaxWidth(Double.MAX_VALUE);
				productOptions.getChildren().add(rentBtn);
			}
		} catch (EntityNotFoundException e) {
			//we don't want to spam the user with the alerts every time he rate or review the movie
			if(notFirstTimeInsidePage == false) {
				//catch when a product of the movie does not exists, it means that the movie is currently not for purchasing
				if(!isPurchased && !isRented) {
					//if the movie is not available to purchase, and the user does not have an option to watch it, then alert him
			        AppUtils.alertOfError("Product Not available", "The movie in unavailable for purchases and rents");
				}
			}
		}
	}
	
	/**
	 * Configures the appearance and behavior of the "Watch Movie" button.
	 * <p>This method styles the button with a pink border, white background, and rounded corners. 
	 * It also sets up an event handler to open a modal window displaying a still image of the movie 
	 * when the button is clicked. The event handler verifies that the user is allowed to watch the movie 
	 * and handles any errors if the movie is not found or if the user cannot watch it.</p>
	 * 
	 * @param watchMovieButton the {@link TextFlow} element to be styled and configured
	 */
	private void setWatchMovieButton(TextFlow watchMovieButton) {
		watchMovieButton.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
	            new BorderWidths(1))));
		watchMovieButton.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-radius: 5px; -fx-background-radius: 5px;");
		watchMovieButton.setPadding(new Insets(5));
		watchMovieButton.setCursor(Cursor.HAND);
		watchMovieButton.setOnMouseClicked(e -> {
			if(movie == null)
				return;
			Long movieId = movie.getId();
			try {
				// We will verify again that the current user can watch the movie.
				moviePurchasedController.checkIfCanWatchMovie(movieId);
				// We add a still image of the movie, in order to simulate a watching movie video experience (without needing to load the actual movie video).
				Stage watchMoviePage = new Stage();
	            AppImageUtils.loadAppIconImage(watchMoviePage);
	            watchMoviePage.initModality(Modality.APPLICATION_MODAL);
	            watchMoviePage.initOwner(App.getApplicationInstance().getStage());
	            BorderPane movieVideoStill = new BorderPane();
	            movieVideoStill.setBackground(backgroundView.getBackground());
	            Scene scene = new Scene(movieVideoStill);
	            watchMoviePage.setTitle("Watch Movie: " + nameLbl.getText());
	            watchMoviePage.setScene(scene);
	            watchMoviePage.show();
			} catch (EntityNotFoundException e1) {
				AppUtils.alertOfError("Can't Watch Movie", "The Movie with id \""+movieId+"\" is not found or the user can't watch it (No Active Purchases)");
			}
		});
	}
	
	/**
	 * <p>Starts a countdown timer that updates the specified {@link Text} element with the remaining time for a movie rental.</p>
	 * 
	 * <p>If the rental period has expired, an alert will be shown informing the user that the rental is over and offering options
	 * to purchase or rent the movie again. The countdown stops if the rental time is negative or if the user navigates away.</p>
	 * 
	 * @param currentRentTime The start time of the rental period.
	 * @param remainTimeText The {@link Text} element that displays the remaining rental time.
	 */
	private void createRentCountdown(LocalDateTime currentRentTime, Text remainTimeText) {
		if(remainingRentTime != null)
			remainingRentTime.stop();
		remainingRentTime = new Timeline();
		KeyFrame keyFrame = new KeyFrame(javafx.util.Duration.seconds(0), new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				Duration timeLeft = MoviePageUtils.getRemainTime(currentRentTime);
				if(timeLeft == null || timeLeft.isNegative() || mainPane.getParent() == null) {
					remainingRentTime.stop();
					if(timeLeft != null && timeLeft.isNegative()) {
						Alert alert = AppUtils.alertOfInformation("Rent of the movie is over", "in order to watch movie purchase it or rent it again");
	    		        alert.setOnCloseRequest(new EventHandler<DialogEvent>() {
							
							@Override
							public void handle(DialogEvent event) {
								initializeMovie(movie);
							}
						});
					}
				}
				else
					remainTimeText.setText(DataUtils.durationToString(timeLeft));
			}
			
		});
		remainingRentTime.getKeyFrames().addAll(keyFrame, new KeyFrame(javafx.util.Duration.seconds(1)));
        remainingRentTime.setCycleCount(Timeline.INDEFINITE);
        remainingRentTime.play();
	}
	
	/**
	 * Updates the ratings label displayed on the `ratingButton`. This method clears the existing children of the button,
	 * then attempts to fetch and display the user's rating for the current movie. If the user's rating is found, it is
	 * displayed along with the text "Your Rating". If the user has not rated the movie or is not logged in, a default
	 * message inviting the user to rate the movie is shown instead.
	 */
	private void setRatingsLabel() {
		ratingButton.getChildren().clear();
		try {
			MovieReviewReference moviewReview = movieReviewController.getMovieReviewOfUser(movie.getId());
			ratingButton.getChildren().addAll(MoviePageUtils.getUserRating(moviewReview));
			ratingButton.getChildren().add(new Text("  Your Rating"));
		} catch (EntityNotFoundException|UserNotLoggedInException e) {
			// EntityNotFoundException: the logged user didn't rate already
			// UserNotLoggedInException: a guest can still see the user page, but cannot rate
			Text star = new Text("☆");
			star.setFill(Color.BLUE);
			star.setFont(Font.font(star.getFont().getSize()+3));
			Text rateText = new Text("Rate movie");
			ratingButton.getChildren().addAll(star, rateText);
		}
	}
	
	/**
	 * <p>Adds a product to the shopping cart with a specified action (buy or rent).</p>
	 * 
	 * <p>This method creates a new {@link CartProductReference} with the product's ID and the action type (buy or rent), then 
	 * attempts to add it to the cart through the {@link cartController}. If the product is already in the cart with the same 
	 * action type, or if an error occurs while adding the product, an error alert is shown to the user.</p>
	 * 
	 * @param product The {@link ProductDto} representing the product to be added to the cart.
	 * @param isBuying {@code true} if the product is being bought; {@code false} if the product is being rented.
	 */
	private void addToCart(ProductDto product, boolean isBuying) {
		CartProductReference dto = new CartProductReference();
		dto.setProductId(product.getId());
		dto.setBuying(isBuying);
		try {
			cartController.addProductToCart(dto);
		} catch (EntityNotFoundException e1) {
			//if there is a problem with adding the product to the cart
			AppUtils.alertOfError("Add Product to Cart error", e1.getMessage());
		} catch (EntityAlreadyExistsException e1) {
			//if the product is already in cart with the same purchase type (buy or rent), send an error message
			// that triggers in buy after product is already but, or the same with rent
			AppUtils.alertOfError("Add Product to Cart error", e1.getMessage());
		}
	}
	
	/**
	 * <p>Opens a modal window for adding a rating to the current movie.</p>
	 * 
	 * <p>This method creates a new instance of {@link MovieReviewPage} configured for adding ratings only. 
	 * The user will be able to provide a numeric rating for the movie.</p>
	 */
	@FXML
	private void openAddRatingsPage() {
		new MovieReviewPage(false, this);
	}
	
	/**
	 * <p>Opens a modal window for adding a review and rating to the current movie.</p>
	 * 
	 * <p>This method creates a new instance of {@link MovieReviewPage} configured for adding both a review and rating.
	 * The user will be able to provide a title, content, and numeric rating for the movie.</p>
	 */
	@FXML
	private void openAddReviewPage() {
		new MovieReviewPage(true, this);
	}
	
	/**
	 * <p>A custom cell for displaying a {@link Person} in a {@link ListView}. This cell uses a {@link PersonPane} to render
	 * the graphical representation of the person object.</p>
	 * 
	 * <p>The cell's appearance is updated based on whether the item is null or not. When an item is present, the cell displays
	 * the {@link PersonPane} with the item's data. When the item is null or the cell is empty, the cell is reset and no graphical
	 * content is shown.</p>
	 */
	private class PersonCell<T> extends ListCell<T> {
		
		private PersonPane personPane;
		
		/**
		 * <p>Constructs a {@link PersonCell} instance with a new {@link PersonPane} for rendering the item.</p>
		 * 
		 * <p>The cell's padding is set to zero, and a new {@link PersonPane} is initialized using the provided {@link mainPane}.</p>
		 */
		public PersonCell() {
			this.personPane = new PersonPane(mainPane);
			setStyle("-fx-padding: 0px;");
		}
		
	    /**
	     * <p>Updates the cell's graphical representation based on the provided item.</p>
	     * 
	     * <p>If the item is {@code null} or the cell is empty, the cell's graphic is set to {@code null} and the 
	     * {@link PersonPane} is reset. Otherwise, the cell displays the {@link PersonPane} with the item's data.</p>
	     * 
	     * @param item The item to be displayed in the cell, or {@code null} if the cell is empty.
	     * @param empty {@code true} if the cell is empty, otherwise {@code false}.
	     */
	    @Override
	    public void updateItem(T item, boolean empty) {
	        super.updateItem(item, empty);
	        if (item == null || empty) {
	            setGraphic(null);
	            setText(null);
	            personPane.reset();
	        }
	        else {
	            setGraphic(personPane);
	            setText(null);
	            personPane.set(item);
	        }
	        setAlignment(Pos.CENTER_LEFT);
	    }
	}
}