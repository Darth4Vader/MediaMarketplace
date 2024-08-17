package frontend.moviePage;

import java.util.Map;
import java.util.Map.Entry;

import backend.DataUtils;
import backend.controllers.MovieReviewController;
import backend.dtos.MovieDto;
import backend.dtos.references.MovieRatingReference;
import backend.dtos.references.MovieReviewReference;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.MovieReviewValuesAreIncorrectException;
import backend.exceptions.enums.MovieReviewTypes;
import frontend.App;
import frontend.utils.AppUtils;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

/**
 * <p>Represents a stage for adding or editing movie reviews and ratings.</p>
 * 
 * <p>This class creates a modal window allowing users to submit ratings and reviews for a movie. Depending on the
 * {@code isReview} parameter, the window can be configured to allow users to add just a rating or both a rating and a 
 * review title and content.</p>
 * 
 * <p>The window will prepopulate fields with existing data if available and will handle validation for input fields.
 * It also interacts with the {@link movieReviewController} to add or update movie reviews and ratings.</p>
 * 
 * @param isReview {@code true} if the window is for adding or editing a review; {@code false} if it is only for adding a rating.
 */
public class MovieReviewPage extends Stage {
	
	private TextField titleField;
	private TextArea contentArea;
	private BorderPane titleTextBox;
	private BorderPane contentTextBox;
	
	/**
	 * <p>Constructs a {@link MovieReviewPage} instance.</p>
	 * 
	 * <p>If the {@code isReview} parameter is {@code true}, the page allows users to add a review title and content
	 * along with the rating. If {@code isReview} is {@code false}, the page only allows adding a rating.</p>
	 * 
	 * <p>Prepopulates the fields with existing review data if available. Handles errors related to adding or updating
	 * reviews and ratings, and provides validation feedback to the user.</p>
	 * 
	 * @param isReview {@code true} if the page is for adding a review; {@code false} if it is only for adding a rating.
	 */
	public MovieReviewPage(boolean isReview, MoviePageController moviePageController) {
		if(moviePageController == null || moviePageController.movie == null)
			return;
		MovieDto movie = moviePageController.movie;
		Integer ratings = null;
		String reviewTitle = "", reviewContent = "";
		MovieReviewController movieReviewController = moviePageController.movieReviewController;
		try {
			MovieReviewReference moviewReview = movieReviewController.getMovieReviewOfUser(movie.getId());
			ratings = moviewReview.getRating();
			reviewTitle = moviewReview.getReviewTitle();
			reviewContent = moviewReview.getReview();
		} catch (EntityNotFoundException e) {
			//it's okay, like if a user never reviewed this movie, then the exception will activate
			//so we don't need to handle the exception, because it is a possibility 
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
			MovieRatingReference movieRatingReference;
			if(isReview)
				movieRatingReference = new MovieReviewReference();
			else
				movieRatingReference = new MovieRatingReference();
			movieRatingReference.setMovieId(movie.getId());
			movieRatingReference.setRating(DataUtils.getIntegerNumber(ratingsField.getText()));
			try {
				if(isReview) {
					if(movieRatingReference instanceof MovieReviewReference) {
						MovieReviewReference movieReviewReference = (MovieReviewReference) movieRatingReference;
						movieReviewReference.setReviewTitle(titleField.getText());
						movieReviewReference.setReview(contentArea.getText());
						movieReviewController.addMovieReviewOfUser(movieReviewReference);
					}
				}
				else {
					movieReviewController.addMovieRatingOfUser(movieRatingReference);
				}
				moviePageController.initializeMovie(movie);
				this.close();
			} catch (MovieReviewValuesAreIncorrectException e1) {
				//if there is a problem with adding the review, then we will display the user with the reasons
				Map<MovieReviewTypes, String> map = e1.getMap();
				for(Entry<MovieReviewTypes, String> entry : map.entrySet()) {
					String val = entry.getValue();
					switch (entry.getKey()) {
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
	
	/**
	 * <p>Filters the input in the rating field to ensure only valid ratings are accepted.</p>
	 * 
	 * <p>Ratings must be numeric, between 1 and 100, and no more than 3 digits long.</p>
	 * 
	 * @param change The proposed change to the rating field.
	 * @return The modified change if it is valid, or an empty change if invalid.
	 */
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
    
	/**
	 * <p>Displays validation messages for input fields.</p>
	 * 
	 * <p>Shows an error message below the specified {@link BorderPane} if the corresponding input field is invalid.</p>
	 * 
	 * @param textInput The {@link TextInputControl} to validate.
	 * @param pane The {@link BorderPane} where the validation message will be displayed.
	 * @param errorMessage The error message to display if the input is invalid.
	 */
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