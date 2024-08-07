package frontend.moviePage;

import java.net.MalformedURLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.DataUtils;
import backend.controllers.CartController;
import backend.controllers.MoviePurchasedController;
import backend.controllers.MovieReviewController;
import backend.controllers.ProductController;
import backend.dto.cart.CartProductDto;
import backend.dto.mediaProduct.MovieReviewDto;
import backend.entities.Actor;
import backend.entities.Director;
import backend.entities.Movie;
import backend.entities.MoviePurchased;
import backend.entities.MovieReview;
import backend.entities.Product;
import backend.exceptions.DtoValuesAreIncorrectException;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.UserNotLoggedInException;
import backend.exceptions.enums.MovieReviewTypes;
import frontend.App;
import frontend.AppUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.TextInputControl;
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
import javafx.util.converter.IntegerStringConverter;

@Component
public class MoviePageController {
	
	public static final String PATH = "/frontend/moviePage/MoviePage.fxml";
	
	@FXML
	private ScrollPane mainPane;
	
	@FXML
	private BorderPane backgroundView;
	
	@FXML
	private ImageView posterView;
	
	@FXML
	private VBox productOptions;
	
	@FXML
	private TextArea synopsisArea;
	
	@FXML
	private Label nameLbl;
	
	@FXML
	private TextFlow ratingButton;
	
	@FXML
	private Label yearLbl;
	
	@FXML
	private Label rutimeLbl;
	
	@FXML
	private Label movieRatingNumberLbl;
	
	@FXML
	private HBox watchOptions;
	
	@FXML
	private ListView<Director> directorsListView;
	
	@FXML
	private ListView<Actor> actorsListView;
	
	@FXML
	private ListView<MovieReview> reviewsListView;
	
	@Autowired
	private ProductController productController;
	
	@Autowired
	private MoviePurchasedController moviePurchasedController;
	
	@Autowired
	private CartController cartController;
	
	@Autowired
	private MovieReviewController movieReviewController;
	
	private Movie movie;
	
	private Timeline remainingRentTime;
	
	private ObservableList<Director> directorsList;
	
	private ObservableList<Actor> actorsList;
	
	private ObservableList<MovieReview> movieReviewsList;
	
	@FXML
	private void initialize() {
		directorsList = FXCollections.observableArrayList();
		directorsListView.setCellFactory(x -> new PersonCell<Director>());
		directorsListView.setItems(directorsList);
		directorsListView.setSelectionModel(null);
		directorsListView.prefHeightProperty().bind(mainPane.heightProperty().multiply(0.3).add(80));
		
		actorsList = FXCollections.observableArrayList();
		actorsListView.setCellFactory(x ->  new PersonCell<Actor>());
		actorsListView.setItems(actorsList);
		actorsListView.setSelectionModel(null);
		actorsListView.prefHeightProperty().bind(mainPane.heightProperty().multiply(0.3).add(80));
		
		movieReviewsList = FXCollections.observableArrayList();
		reviewsListView.setCellFactory(x -> new MovieReviewCell());
		reviewsListView.setItems(movieReviewsList);
		reviewsListView.setSelectionModel(null);
		reviewsListView.prefHeightProperty().bind(mainPane.heightProperty().multiply(0.4));
	}
	
	/**
	 * Initializes the movie pane
	 * @param movie the movie to use
	 * @throws MalformedURLException
	 */
	public void initializeMovie(Movie movie) {
		this.movie = movie;
		addPurchaseButtons();
		ratingButton.getChildren().clear();
		try {
			MovieReview moviewReview = movieReviewController.getMovieReviewOfUser(movie.getId());
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
		String backdropPath = movie.getBackdropPath(); 
		if(DataUtils.isNotBlank(backdropPath)) {
			BackgroundSize backgroundSize = new BackgroundSize(1,
					1,
			        true,
			        true,
			        false,
			        false);
			BackgroundImage backgroudImage = new BackgroundImage(AppUtils.loadImageFromClass(movie.getBackdropPath()),
			        BackgroundRepeat.NO_REPEAT,
			        BackgroundRepeat.NO_REPEAT,
			        BackgroundPosition.CENTER,
			        backgroundSize);
			backgroundView.setBackground(new Background(backgroudImage));
		}
		posterView.setImage(AppUtils.loadImageFromClass(movie.getPosterPath()));
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
		directorsList.clear();
		List<Director> directors = movie.getDirectors();
		if(directors != null) {
			directorsList.setAll(directors);
		}
		actorsList.clear();
		List<Actor> actors = movie.getActorsRoles();
		if(actors != null) {
			actorsList.setAll(actors);
		}
		movieReviewsList.clear();
		try {
			List<MovieReview> reviews = movieReviewController.getAllReviewOfMovie(movie.getId());
			if(reviews != null && !reviews.isEmpty()) {
				int ratingNumber = (int) MovieReviewController.calculateRating(reviews);
				movieRatingNumberLbl.setText(""+ratingNumber);
				for(MovieReview review : reviews) {
					if(DataUtils.isNotBlank(review.getReviewTitle())) {
						movieReviewsList.add(review);	
					}
				}
			}
		} catch (EntityNotFoundException e) {
			//it's okay, a movie can be not reviewed
		}
	}
	
	private void addPurchaseButtons() {
		boolean isPurchased = false;
		boolean isRented = false;
		if(remainingRentTime != null)
			remainingRentTime.stop();
		productOptions.getChildren().clear();
		try {
			LocalDateTime currentRentTime = null;
			List<MoviePurchased> purchasedLists = moviePurchasedController.getActiveListUserMovie(movie.getId());
			for(MoviePurchased purchased : purchasedLists) {
				if(!purchased.isRented()) {
					isPurchased = true;
					break;
				}
				else {
					isRented = true;
					currentRentTime = purchased.getCurrentRentTime();
				}
			}
			if(isPurchased || isRented) {
				TextFlow btnTextFlow = new TextFlow();
				btnTextFlow.getChildren().add(new Text("Watch Movie"));
				if(!isPurchased && isRented) {
					Text remainTimeText = new Text();
					btnTextFlow.getChildren().addAll(new Text(" (Rent)\nRemaining Time: "), remainTimeText);
					createRentCountdown(currentRentTime, remainTimeText);
				}
				
				btnTextFlow.setBorder(new Border(new BorderStroke(Color.PINK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
			            new BorderWidths(1))));
				btnTextFlow.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-radius: 5px; -fx-background-radius: 5px;");
				btnTextFlow.setPadding(new Insets(5));
				btnTextFlow.setCursor(Cursor.HAND);
				productOptions.getChildren().add(btnTextFlow);
			}
		} catch (EntityNotFoundException e) {
			//This is okay, no need to add exception handling, the product will not be owned by the user
		}
		catch (UserNotLoggedInException e) {
			//Can be if the user is not logged in, let him watch, but he can't buy as a guest
		}
		if(!isPurchased) try {
			Product product = productController.getProductByMovieId(movie.getId());
			Button buyBtn = new Button("Buy for: " + product.calculatePrice(true));
			buyBtn.setWrapText(true);
			buyBtn.setOnAction(e -> {
				addToCart(product, true);
			});
			buyBtn.setMaxWidth(Double.MAX_VALUE);
			productOptions.getChildren().add(buyBtn);
			if(!isRented) {
				Button rentBtn = new Button("Rent for: " + product.calculatePrice(false));
				rentBtn.setWrapText(true);
				rentBtn.setOnAction(e -> {
					addToCart(product, false);
				});
				rentBtn.setMaxWidth(Double.MAX_VALUE);
				productOptions.getChildren().add(rentBtn);
			}
		} catch (EntityNotFoundException e) {
			//catch when a product of the movie does not exists, it means that the movie is currently not for purchasing
			if(!isPurchased && !isRented) {
				//if the movie is not available to purchase, and the user does not have an option to watch it, then alert him
		        AppUtils.alertOfError("Product Not available", "The movie in unavailable for purchases and rents");
			}
		}
	}
	
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
	
	private void addToCart(Product product, boolean isBuying) {
		CartProductDto dto = new CartProductDto();
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
	
	@FXML
	private void openAddRatingsPage() {
		new MovieReviewPage(false);
	}
	
	@FXML
	private void openAddReviewPage() {
		new MovieReviewPage(true);
	}
	
	private class MovieReviewPage extends Stage {
		
		private TextField titleField;
		private TextArea contentArea;
		private BorderPane titleTextBox;
		private BorderPane contentTextBox;
		
		public MovieReviewPage(boolean isReview) {
			if(movie == null)
				return;
			Integer ratings = null;
			String reviewTitle = "", reviewContent = "";
			try {
				MovieReview moviewReview = movieReviewController.getMovieReviewOfUser(movie.getId());
				ratings = moviewReview.getRating();
				reviewTitle = moviewReview.getReviewTitle();
				reviewContent = moviewReview.getReview();
			} catch (EntityNotFoundException e) {
				//it's okay, like if a user never reviewed this movie, then the exception will activate
			}
			VBox box = new VBox();
			Label ratingsText = new Label("Add Ratings");
			BorderPane ratingsTextBox = new BorderPane(ratingsText);
			TextField ratingsField = new TextField();
			ratingsField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), null, this::filter));
			if(ratings != null)
				ratingsField.setText(""+ratings);
			box.getChildren().addAll(ratingsTextBox, ratingsField);
			String btnText;
			//TextField titleField = null;
			//TextArea contentArea = null; 
			//
			//BorderPane titleTextBox = null, contentTextBox = null;
			if(isReview) {
				Label titleText = new Label("Add Titles");
				titleTextBox = new BorderPane(titleText);
				titleField = new TextField(reviewTitle);
				Label contentText = new Label("Write The Review");
				contentTextBox = new BorderPane(contentText);
				contentArea = new TextArea(reviewContent);
				box.getChildren().addAll(titleTextBox, titleField, contentTextBox, contentArea);
				btnText = "Add Review";
			}
			else
				btnText = "Add Ratings";
			
			Button addBtn = new Button(btnText);
			box.getChildren().addAll(addBtn);
			Scene scene = new Scene(box);
			addBtn.setOnAction(e -> {
				MovieReviewDto movieReviewDto = new MovieReviewDto();
				movieReviewDto.setMovieId(movie.getId());
				movieReviewDto.setRating(DataUtils.getIntegerNumber(ratingsField.getText()));
				try {
					if(isReview) {
						movieReviewDto.setReviewTitle(titleField.getText());
						movieReviewDto.setReview(contentArea.getText());
						movieReviewController.addMovieReviewOfUser(movieReviewDto);
					}
					else {
						movieReviewController.addMovieRatingOfUser(movieReviewDto);
					}
					initializeMovie(movie);
					this.close();
				} catch (DtoValuesAreIncorrectException e1) {
					//if there is a problem with adding the review, then we will display the user with the reasons
					Map<String, String> map = e1.getMap();
					for(Entry<String, String> entry : map.entrySet()) {
						String val = entry.getValue();
						switch (MovieReviewTypes.valueOf(entry.getKey())) {
						case CREATED_DATE:
							break;
						case RATING:
							bindValidation(ratingsField, ratingsTextBox, val);
							break;
						case REVIEW:
							bindValidation(contentArea, contentTextBox, val);
							break;
						case TITLE:
							bindValidation(titleField, titleTextBox, val);
							break;
						default:
							break;
						}
					}
				} catch (EntityNotFoundException e1) {
					//can happen if the movie is removed from the database, and the user is trying to add to it a review
					AppUtils.alertOfError("Review addition Problem", e1.getMessage());
				}
			});
			this.setScene(scene);
			this.initModality(Modality.APPLICATION_MODAL);
			this.initOwner(App.getApplicationInstance().getStage());
			this.show();
		}
		
	    private Change filter(Change change) {
	    	String text = change.getControlNewText();
	    	boolean b = true;
	        if (text.matches("\\d*") && text.length() <= 3) {
	        	Integer val = DataUtils.getIntegerNumber(text);
	        	if(val != null && val >= 1 && val <= 100) {
	        		b = false;
	        	}
	        }
	        if(b)
	        	change.setText("");
	        return change;
	    }
	    
		private void bindValidation(TextInputControl textInput, BorderPane pane, String errorMessage) {
			if(textInput == null || pane == null)
				return;
			Label validate = new Label(errorMessage);
			validate.setVisible(true);
			validate.setTextFill(Color.RED);
			pane.setBottom(validate);
			StringProperty property = textInput.textProperty();
			ChangeListener<String> listener = new ChangeListener<String>() {
				
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
					if(DataUtils.isNotBlank(newValue)) {
						validate.setVisible(false);
						pane.setBottom(null);
						property.removeListener(this);
					}
				}
			};
			property.addListener(listener);
		}
	}
	
	private class PersonCell<T> extends ListCell<T> {
		
		private PersonPane personPane;
		
		public PersonCell() {
			this.personPane = new PersonPane(mainPane);
			setStyle("-fx-padding: 0px;");
		}
		
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
			setBorder(new Border(new BorderStroke(Color.PINK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
		            new BorderWidths(1))));
	    }
	}
}
