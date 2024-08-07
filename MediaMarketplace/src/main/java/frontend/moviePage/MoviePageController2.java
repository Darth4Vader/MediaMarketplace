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
import backend.entities.Person;
import backend.entities.Product;
import backend.entities.User;
import backend.exceptions.DtoValuesAreIncorrectException;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.enums.MovieReviewTypes;
import frontend.App;
import frontend.AppUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

@Component
public class MoviePageController2 {
	
	public static final String PATH = "/frontend/help/MoviePanel.fxml";
	
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
	private HBox directorsBox;
	
	@FXML
	private HBox actorsBox;
	
	@FXML
	private VBox reviewsBox;
	
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
	
	/**
	 * Initializes the movie pane
	 * @param movie the movie to use
	 * @throws MalformedURLException
	 */
	public void initializeMovie(Movie movie) throws MalformedURLException {
		this.movie = movie;
		boolean isPurchased = false;
		boolean isRented = false;
		if(remainingRentTime != null)
			remainingRentTime.stop();
		try {
			LocalDateTime timeSince = null;
			productOptions.getChildren().clear();
			List<MoviePurchased> purchasedLists = moviePurchasedController.getActiveListUserMovie(movie.getId());
			for(MoviePurchased purchased : purchasedLists) {
				if(!purchased.isRented()) {
					isPurchased = true;
					break;
				}
				else {
					isRented = true;
					timeSince = purchased.getCurrentRentTime();
				}
			}
			if(isPurchased || isRented) {
				Button viewButton = new Button();
				String buttonText = "Watch Movie";
				if(!isPurchased && isRented) {
					buttonText += " (Rent)\n Remaining Time: ";
					final String buttonTextFinal = buttonText;
					final LocalDateTime timeSinceFinal = timeSince;
					remainingRentTime = new Timeline();
					KeyFrame keyFrame = new KeyFrame(javafx.util.Duration.seconds(0), new EventHandler<ActionEvent>() {
						
						@Override
						public void handle(ActionEvent event) {
							Duration timeLeft = getRemainTime(timeSinceFinal);
							if(timeLeft.isNegative() || mainPane.getParent() == null) {
								remainingRentTime.stop();
								if(timeLeft.isNegative()) {
				    		        Alert alert = new Alert(Alert.AlertType.INFORMATION);
				    		        alert.setTitle("Rent of the movie is over");
				    		        alert.setHeaderText("in order to watch movie purchase it or rent it again");
				    		        alert.show();
				    		        alert.setOnCloseRequest(new EventHandler<DialogEvent>() {
										
										@Override
										public void handle(DialogEvent event) {
											try {
												initializeMovie(movie);
											} catch (MalformedURLException e) {
											}
										}
									});
								}
							}
							else
								viewButton.setText(buttonTextFinal+DataUtils.durationToString(timeLeft));
						}
						
					});
					remainingRentTime.getKeyFrames().addAll(keyFrame, new KeyFrame(javafx.util.Duration.seconds(1)));
			        remainingRentTime.setCycleCount(Timeline.INDEFINITE);
			        remainingRentTime.play();
				}
				viewButton.setText(buttonText);
				viewButton.setWrapText(true);
				productOptions.getChildren().add(viewButton);
			}
		} catch (EntityNotFoundException e) {
			//This is okay, no need to add exception handling, the product will not be owned by the user
		}
		if(!isPurchased) try {
			Product product = productController.getProductByMovieId(movie.getId());
			Button buyBtn = new Button("Buy for: " + product.calculatePrice(true));
			buyBtn.setWrapText(true);
			buyBtn.setOnAction(e -> {
				addToCart(product, true);
			});
			productOptions.getChildren().add(buyBtn);
			if(!isRented) {
				Button rentBtn = new Button("Rent for: " + product.calculatePrice(false));
				rentBtn.setWrapText(true);
				rentBtn.setOnAction(e -> {
					addToCart(product, false);
				});
				productOptions.getChildren().add(rentBtn);
			}
		} catch (EntityNotFoundException e) {
			if(!isPurchased && !isRented) {
		        Alert alert = new Alert(Alert.AlertType.ERROR);
		        alert.setTitle("Product Not available");
		        alert.setHeaderText("The movie in unavailable for purchases and rents");
			}
		}
		ratingButton.getChildren().clear();
		try {
			MovieReview moviewReview = movieReviewController.getMovieReviewOfUser(movie.getId());
			ratingButton.getChildren().addAll(getUserRating(moviewReview));
			ratingButton.getChildren().add(new Text("  Your Rating"));
		} catch (EntityNotFoundException e) {
			Text star = new Text("☆");
			star.setFill(Color.BLUE);
			star.setFont(Font.font(star.getFont().getSize()+3));
			Text rateText = new Text("Rate movie");
			ratingButton.getChildren().addAll(star, rateText);
		}
		String backdropPath = movie.getBackdropPath(); 
		if(DataUtils.isNotBlank(backdropPath)) try {
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
		catch (MalformedURLException e) {
			e.printStackTrace();
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
		List<Director> directors = movie.getDirectors();
		if(directors != null) {
			directorsBox.getChildren().clear();
			for(Director director : directors) {
				addToDirectorsPane(director);
			}
		}
		List<Actor> actors = movie.getActorsRoles();
		if(actors != null) {
			actorsBox.getChildren().clear();
			for(Actor actor : actors) {
				addToActorsPane(actor);
			}
		}
		try {
			List<MovieReview> reviews = movieReviewController.getAllReviewOfMovie(movie.getId());
			reviewsBox.getChildren().clear();
			if(reviews != null && !reviews.isEmpty()) {
				int ratingNumber = (int) MovieReviewController.calculateRating(reviews);
				movieRatingNumberLbl.setText(""+ratingNumber);
				for(MovieReview review : reviews) {
					addToReviewsPane(review);
				}
			}
		} catch (EntityNotFoundException e) {
			//it's okay, a movie can be not reviewed
		}
	}
	
	private void addToCart(Product product, boolean isBuying) {
		CartProductDto dto = new CartProductDto();
		dto.setProductId(product.getId());
		dto.setBuying(isBuying);
		try {
			cartController.addProductToCart(dto);
		} catch (EntityNotFoundException e1) {
			e1.printStackTrace();
		} catch (EntityAlreadyExistsException e1) {}
	}
	
	public Duration getRemainTime(LocalDateTime timeSince) {
		LocalDateTime now = LocalDateTime.now();
		try {
			return Duration.between(now, timeSince);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	private TextFlow getUserRating(MovieReview review) {
		TextFlow textFlowPane = new TextFlow();
		Text star = new Text("★");
		star.setFill(Color.BLUE);
		star.setFont(Font.font(star.getFont().getSize()+3));
		Text rating = new Text(" "+review.getRating());
		rating.setStyle("-fx-font-weight: bold; -fx-font-size: 19");
		Text rangeRating = new Text("/100");
		textFlowPane.getChildren().addAll(star, rating, rangeRating);
		return textFlowPane;
	}
	
	private void addToReviewsPane(MovieReview review) {
		if(DataUtils.isBlank(review.getReviewTitle()))
			return;
		VBox box = new VBox();
		TextFlow textFlowPane = getUserRating(review);
		Text title = new Text("    "+ review.getReviewTitle());
		title.setStyle("-fx-font-weight: bold;");
		textFlowPane.getChildren().add(title);
		User user = review.getUser();
		Label userName = new Label(" "+user.getUsername() + "	");
		LocalDateTime createDate = review.getCreatedDate();
		Label createdDateLabel = new Label(DataUtils.getLocalDateTimeInCurrentZone(createDate));
		HBox userInfo = new HBox();
		userInfo.getChildren().addAll(userName, createdDateLabel);
		userInfo.setPadding(new Insets(0, 0, 8, 0));
		Label reviewText = new Label(" "+review.getReview());
		reviewText.setWrapText(true);
		box.getChildren().addAll(textFlowPane, userInfo, reviewText);
		box.setStyle("-fx-border-color: black; -fx-border-radius: 5;");
		reviewsBox.getChildren().add(box);
	}
	
	private void addToActorsPane(Actor actor) {
		Person person = actor.getPerson();
		addToPersonPane(person, person.getName() + "/" + actor.getRoleName(), actorsBox);
	}
	
	private void addToDirectorsPane(Director director) {
		Person person = director.getPerson();
		addToPersonPane(person, person.getName(), directorsBox);
	}
	
	private void addToPersonPane(Person person, String name, Pane pane) {
		VBox actorPane = new VBox();
		Label actorName = new Label(name);
		actorName.setCenterShape(false);
		actorName.setWrapText(true);
		actorName.prefWidthProperty().bind(mainPane.widthProperty().multiply(0.2));
		actorName.setTextAlignment(TextAlignment.CENTER);
		actorName.setStyle("-fx-alignment: center");
		String personImagePath = person.getImagePath();
		if(DataUtils.isNotBlank(personImagePath)) {
			ImageView actorImage;
			try {
				actorImage = new ImageView(AppUtils.loadImageFromClass(personImagePath));
				actorImage.fitWidthProperty().bind(mainPane.widthProperty().multiply(0.2));
				actorImage.fitHeightProperty().bind(mainPane.heightProperty().multiply(0.3).subtract(50));
				actorPane.getChildren().add(actorImage);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
			}
		}
		actorPane.getChildren().add(actorName);
		actorPane.setStyle("-fx-border-color: blue");
		pane.getChildren().add(actorPane);
	}
	
	@FXML
	private void openAddRatingsPage() throws MalformedURLException {
		if(movie == null)
			return;
		Integer ratings = null;
		try {
			MovieReview moviewReview = movieReviewController.getMovieReviewOfUser(movie.getId());
			ratings = moviewReview.getRating();
		} catch (EntityNotFoundException e) {
			//it's okay, like if a user never rated this movie, then the exception will activate
		}
		VBox box = new VBox();
		Label ratingsText = new Label("Add Ratings");
		BorderPane ratingsTextBox = new BorderPane(ratingsText);
		TextField ratingsField = new TextField();
		ratingsField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), null, this::filter));
		if(ratings != null)
			ratingsField.setText(""+ratings);
		Button addBtn = new Button("Add Ratings");
		box.getChildren().addAll(ratingsTextBox, ratingsField, addBtn);
		Scene scene = new Scene(box);
		Stage stage = new Stage();
		addBtn.setOnAction(e -> {
			MovieReviewDto movieReviewDto = new MovieReviewDto();
			movieReviewDto.setMovieId(movie.getId());
			movieReviewDto.setRating(DataUtils.getIntegerNumber(ratingsField.getText()));
			try {
				movieReviewController.addMovieRatingOfUser(movieReviewDto);
				initializeMovie(movie);
				stage.close();
			} catch (DtoValuesAreIncorrectException e1) {
				Map<String, String> map = e1.getMap();
				for(Entry<String, String> entry : map.entrySet()) {
					String val = entry.getValue();
					switch (MovieReviewTypes.valueOf(entry.getKey())) {
					case RATING:
						bindValidation(ratingsField, ratingsTextBox, val);
						break;
					default:
						break;
					}
				}
			} catch (EntityNotFoundException e1) {
				e1.printStackTrace();
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		stage.setScene(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(App.getApplicationInstance().getStage());
		stage.show();
		Platform.runLater( () -> box.requestFocus() );
	}
	
	@FXML
	private void openAddReviewPage() throws MalformedURLException {
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
		Label titleText = new Label("Add Titles");
		BorderPane titleTextBox = new BorderPane(titleText);
		TextField titleField = new TextField(reviewTitle);
		Label contentText = new Label("Write The Review");
		BorderPane contentTextBox = new BorderPane(contentText);
		TextArea contentArea = new TextArea(reviewContent);
		Button addBtn = new Button("Add Review");
		box.getChildren().addAll(ratingsTextBox, ratingsField, titleTextBox, titleField, contentTextBox, contentArea, addBtn);
		Scene scene = new Scene(box);
		Stage stage = new Stage();
		addBtn.setOnAction(e -> {
			MovieReviewDto movieReviewDto = new MovieReviewDto();
			movieReviewDto.setMovieId(movie.getId());
			movieReviewDto.setRating(DataUtils.getIntegerNumber(ratingsField.getText()));
			movieReviewDto.setReviewTitle(titleField.getText());
			movieReviewDto.setReview(contentArea.getText());
			try {
				movieReviewController.addMovieReviewOfUser(movieReviewDto);
				initializeMovie(movie);
				stage.close();
			} catch (DtoValuesAreIncorrectException e1) {
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
				e1.printStackTrace();
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		stage.setScene(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(App.getApplicationInstance().getStage());
		stage.show();
		Platform.runLater( () -> box.requestFocus() );
		//r.
	}
	
	private void bindValidation(TextInputControl textInput, BorderPane pane, String errorMessage) {
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
	
	public static ChangeListener<String> ratingsPropertyChangeListener(TextInputControl control) {
		return new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		    	try {
			    	boolean b = true;
			        if (newValue.matches("\\d*") && newValue.length() <= 3) {
			        	Integer val = DataUtils.getIntegerNumber(newValue);
			        	if(val != null && val >= 1 && val <= 100)
			        		b = false;
			        }
			        if(b)
			        	newValue = oldValue;
			        if(DataUtils.isNotBlank(newValue))
			        	control.setText(newValue);
		    	}
		    	catch (Throwable e) {
					// TODO: handle exception
				}
		    }
	    };
	}

}
