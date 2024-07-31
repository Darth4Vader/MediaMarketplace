package frontend.help;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
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
import javafx.util.converter.NumberStringConverter;

@Component
public class MoviePageController {
	
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
	private HBox watchOptions;
	
	@FXML
	private HBox directorsBox;
	
	@FXML
	private HBox actorsBox;
	
	@FXML
	private VBox reviewsBox;
	
	/*@FXML
	private void initialize(Movie movie) throws MalformedURLException {
		posterView.setImage(AppUtils.loadImageFromClass(movie.getImagePath()));
		List<Actor> actors = movie.getActorsRoles();
		if(actors != null) for(Actor actor : actors) {
			Person person = actor.getActor();
			BorderPane actorPane = new BorderPane();
			ImageView actorImage = new ImageView(AppUtils.loadImageFromClass(person.getImagePath()));
			Label actorName = new Label(person.getName() + "/" + actor.getRoleName());
			actorPane.setCenter(actorImage);
			actorPane.setBottom(actorName);
			actorsBox.getChildren().add(actorPane);
		}
	}*/
	
	@Autowired
	private ProductController productController;
	
	@Autowired
	private MoviePurchasedController moviePurchasedController;
	
	@Autowired
	private CartController cartController;
	
	@Autowired
	private MovieReviewController movieReviewController;
	
	private Movie movie;
	
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
	
	/*public void refreshMovie(Movie movie) {
		productOptions.getChildren().clear();
		initializeMovie(movie);
	}*/
	
	public void initializeMovie(Movie movie) throws MalformedURLException {
		
		this.movie = movie;
		boolean isPurchased = false;
		boolean isRented = false;
		Duration rentEndIn = null;
		
		try {
			productOptions.getChildren().clear();
			List<MoviePurchased> purchasedLists = moviePurchasedController.getActiveListUserMovie(movie.getId());
			System.out.println("I purchased: " + movie.getName());
			System.out.println(purchasedLists);
			for(MoviePurchased purchased : purchasedLists) {
				System.out.println("Luke Son: " + purchased.isRented());
				if(!purchased.isRented()) {
					isPurchased = true;
					break;
				}
				else {
					isRented = true;
					rentEndIn = purchased.getRemainTime();
				}
			}
			String buttonText = "Watch Movie";
			System.out.println(isRented);
			if(!isPurchased && isRented)
				buttonText += " (Rent)\n Remaining Time: "+DataUtils.durationToString(rentEndIn);
			Button viewButton = new Button(buttonText);
			viewButton.setWrapText(true);
			productOptions.getChildren().add(viewButton);
		} catch (EntityNotFoundException e) {}
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
			Label lbl = new Label("Problem With the Product");
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
			Image backdrop = AppUtils.loadImageFromClass(backdropPath);
			System.out.println(backdrop);
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
		catch (Exception e) {
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
		System.out.println(actors);
		//actorsBox.prefHeightProperty().bind(mainPane.heightProperty().multiply(0.3));
		if(actors != null) {
			actorsBox.getChildren().clear();
			for(Actor actor : actors) {
				addToActorsPane(actor);
				/*Person person = actor.getPerson();
				VBox actorPane = new VBox();
				//BorderPane actorPane = new BorderPane();
				actorPane.prefHeightProperty().bind(mainPane.heightProperty().multiply(0.3));
				Label actorName = new Label(person.getName() + "/" + actor.getRoleName());
				String personImagePath = person.getImagePath();
				if(DataUtils.isNotBlank(personImagePath)) {
					ImageView actorImage;
					try {
						actorImage = new ImageView(AppUtils.loadImageFromClass(personImagePath));
						actorImage.fitWidthProperty().bind(mainPane.widthProperty().multiply(0.2));
						//actorImage.fitHeightProperty().bind(actorsBox.prefHeightProperty().subtract(actorName.prefHeightProperty()));
						
						//actorImage.fitHeightProperty().bind(mainPane.heightProperty().multiply(0.3));
						actorImage.fitHeightProperty().bind(mainPane.heightProperty().multiply(0.3).subtract(50));
						
						actorPane.getChildren().add(actorImage);
						//actorPane.setCenter(actorImage);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
					e.printStackTrace();
					}
				}
				System.out.println(person.getName() + "/" + actor.getRoleName());
				//actorPane.setBottom(actorName);
				actorPane.getChildren().add(actorName);
				actorPane.setStyle("-fx-border-color: blue");
				actorsBox.getChildren().add(actorPane);
				*/
			}
		}
		try {
			List<MovieReview> reviews = movieReviewController.getAllReviewOfMovie(movie.getId());
			reviewsBox.getChildren().clear();
			if(reviews != null) 
				for(MovieReview review : reviews) {
					VBox box = new VBox();
					TextFlow textFlowPane = new TextFlow();
					Text star = new Text("★");
					star.setFill(Color.BLUE);
					star.setFont(Font.font(star.getFont().getSize()+3));
					Text rating = new Text(" "+review.getRating());
					rating.setStyle("-fx-font-weight: bold; -fx-font-size: 19");
					//rating.fontProperty().bin
					//rating.setFont(Font.font(rating.getFont().getSize()+2));
					Text rangeRating = new Text("/100");
					Text title = new Text("    "+ review.getReviewTitle());
					title.setStyle("-fx-font-weight: bold;");
					textFlowPane.getChildren().addAll(star, rating, rangeRating, title);
					User user = review.getUser();
					Label userName = new Label(user.getUsername() + "	");
					LocalDateTime createDate = review.getCreatedDate();
					Label createdDateLabel = new Label(DataUtils.getLocalDateTimeInCurrentZone(createDate));
					HBox userInfo = new HBox();
					userInfo.getChildren().addAll(userName, createdDateLabel);
					userInfo.setPadding(new Insets(0, 0, 8, 0));
					Label reviewText = new Label(review.getReview());
					reviewText.setWrapText(true);
					//reviewText.setEditable(false);
					box.getChildren().addAll(textFlowPane, userInfo, reviewText);
					reviewsBox.getChildren().add(box);
				}
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			System.out.println(actors);
	}
	
	private TextFlow getUserRating(MovieReview review) {
		TextFlow textFlowPane = new TextFlow();
		Text star = new Text("★");
		star.setFill(Color.BLUE);
		star.setFont(Font.font(star.getFont().getSize()+3));
		Text rating = new Text(" "+review.getRating());
		rating.setStyle("-fx-font-weight: bold; -fx-font-size: 19");
		//rating.fontProperty().bin
		//rating.setFont(Font.font(rating.getFont().getSize()+2));
		Text rangeRating = new Text("/100");
		textFlowPane.getChildren().addAll(star, rating, rangeRating);
		return textFlowPane;
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
		//BorderPane actorPane = new BorderPane();
		//actorPane.prefHeightProperty().bind(mainPane.heightProperty().multiply(0.3));
		Label actorName = new Label(name);
		actorName.setCenterShape(false);
		actorName.setWrapText(true);
		//TextArea actorName = new TextArea(name);
		actorName.setWrapText(true);
		actorName.prefWidthProperty().bind(mainPane.widthProperty().multiply(0.2));
		actorName.setTextAlignment(TextAlignment.CENTER);
		actorName.setStyle("-fx-alignment: center");
		//actorName.setEditable(false);
		String personImagePath = person.getImagePath();
		if(DataUtils.isNotBlank(personImagePath)) {
			ImageView actorImage;
			try {
				actorImage = new ImageView(AppUtils.loadImageFromClass(personImagePath));
				actorImage.fitWidthProperty().bind(mainPane.widthProperty().multiply(0.2));
				//actorImage.fitHeightProperty().bind(actorsBox.prefHeightProperty().subtract(actorName.prefHeightProperty()));
				
				//actorImage.fitHeightProperty().bind(mainPane.heightProperty().multiply(0.3));
				actorImage.fitHeightProperty().bind(mainPane.heightProperty().multiply(0.3).subtract(50));
				
				actorPane.getChildren().add(actorImage);
				//actorPane.setCenter(actorImage);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
			}
		}
		//actorPane.setBottom(actorName);
		actorPane.getChildren().add(actorName);
		actorPane.setStyle("-fx-border-color: blue");
		pane.getChildren().add(actorPane);
	}
	
	/*
	@FXML
	private void openAddReviewPage() {
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
		}
		VBox box = new VBox();
		
		HBox ratingsTextBox = new HBox();
		Label ratingsText = new Label("Add Ratings");
		Label ratingError = new Label();
		ratingError.setTextFill(Color.RED);
		ratingsTextBox.getChildren().addAll(ratingsText, ratingError);
		TextField ratingsField = new TextField();
		if(ratings != null)
			ratingsField.setText(""+ratings);
		//ratingsField.textProperty().addListener(ratingsPropertyChangeListener(ratingsField));
		ratingsField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), null, this::filter));
		
		HBox titleTextBox = new HBox();
		Label titleText = new Label("Add Titles");
		Label titleError = new Label();
		titleTextBox.getChildren().addAll(titleText, titleError);
		titleError.setTextFill(Color.RED);
		TextField titleField = new TextField(reviewTitle);
		
		HBox contentTextBox = new HBox();
		Label contentText = new Label("Write The Review");
		Label contentError = new Label();
		contentTextBox.getChildren().addAll(contentText, contentError);
		contentError.setTextFill(Color.RED);
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
				stage.close();
			} catch (DtoValuesAreIncorrectException e1) {
				Map<String, String> map = e1.getMap();
				for(Entry<String, String> entry : map.entrySet()) {
					String val = entry.getValue();
					switch (MovieReviewTypes.valueOf(entry.getKey())) {
					case CREATED_DATE:
						break;
					case RATING:
						ratingError.setText(val);
						bindValidation(ratingsField, ratingError);
						break;
					case REVIEW:
						break;
					case TITLE:
						break;
					default:
						break;
					}
				}
			} catch (EntityNotFoundException e1) {
				e1.printStackTrace();
			}
		});
		stage.setScene(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(App.getApplicationInstance().getStage());
		stage.show();
		//r.
	}*/
	
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
		}
		VBox box = new VBox();
		
		Label ratingsText = new Label("Add Ratings");
		BorderPane ratingsTextBox = new BorderPane(ratingsText);
		TextField ratingsField = new TextField();
		ratingsField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), null, this::filter));
		System.out.println(ratings);
		if(ratings != null)
			ratingsField.setText(""+ratings);
		//ratingsField.textProperty().addListener(ratingsPropertyChangeListener(ratingsField));
		
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
		/*final ContextMenu ratingsValidator = new ContextMenu();
        ratingsValidator.setAutoHide(false);*/
        
		/*final ContextMenu passValidator = new ContextMenu();
        passValidator.setAutoHide(false);*/
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
    	System.out.println(text + "()" + text);
        if (text.matches("\\d*") && text.length() <= 3) {
        	Integer val = DataUtils.getIntegerNumber(text);
        	System.out.println("Val: " + val);
        	if(val != null && val >= 1 && val <= 100) {
        		System.out.println("Aproved");
        		b = false;
        	}
        }
        if(b)
        	change.setText("");
        System.out.println("TExt " + change.getText());
        return change;
    }
	
	public static ChangeListener<String> ratingsPropertyChangeListener(TextInputControl control) {
		return new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		    	try {
			    	boolean b = true;
			    	System.out.println(newValue + "()" + oldValue);
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
