package frontend.moviePage;

import java.time.LocalDateTime;

import backend.DataUtils;
import backend.dtos.MovieReviewDto;
import backend.dtos.references.MovieReviewReference;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * A custom {@link ListCell} for displaying {@link MovieReviewDto} objects in a list.
 * <p>This cell presents a movie review with user ratings, review title, user information, creation date, and review text.</p>
 */
class MovieReviewCell extends ListCell<MovieReviewDto> {
	
	/** The main container for the cell's layout. */
	private VBox box;

	/** Displays the user ratings for the movie. */
	private Text userRatings;

	/** Displays the review title. */
	private Text title;

	/** Displays the username of the reviewer. */
	private Label userName;

	/** Displays the creation date of the review. */
	private Label createdDateLabel;

	/** Displays the text content of the review. */
	private Label reviewText;
	
	/**
	 * Constructs a new {@link MovieReviewCell} with default styling and layout.
	 * <p>This constructor initializes the layout components and styles for displaying movie reviews.</p>
	 */
	public MovieReviewCell() {
		setStyle("-fx-padding: 0px;");
		box = new VBox();
		userRatings = new Text();
		TextFlow textFlowPane = MoviePageUtils.getUserRating(userRatings);
		title = new Text();
		title.setStyle("-fx-font-weight: bold;");
		textFlowPane.getChildren().add(title);
		userName = new Label();
		createdDateLabel = new Label("");
		HBox userInfo = new HBox();
		userInfo.getChildren().addAll(userName, createdDateLabel);
		userInfo.setPadding(new Insets(0, 0, 8, 0));
		reviewText = new Label();
		reviewText.setWrapText(true);
		box.getChildren().addAll(textFlowPane, userInfo, reviewText);
		box.setStyle("-fx-border-color: black; -fx-border-radius: 5;");
	}
	
	/**
	 * Sets the content of the cell based on the provided {@link MovieReviewDto}.
	 * <p>This method updates the cell's display to show the review's rating, title, creation date, user name, and review text.</p>
	 * 
	 * @param reviewDto The {@link MovieReviewDto} containing review details to be displayed.
	 */
	public void set(MovieReviewDto reviewDto) {
		MovieReviewReference review = reviewDto.getMovieReview();
		if(review != null) {
			userRatings.setText(""+review.getRating());
			title.setText("    "+ review.getReviewTitle());
			LocalDateTime createDate = review.getCreatedDate();
			createdDateLabel.setText(DataUtils.getLocalDateTimeInCurrentZone(createDate));
			reviewText.setText(" "+review.getReview());
			userName.setText(" "+reviewDto.getUserName() + "	");
		}
	}
	
	/**
	 * Resets the cell's content to be empty.
	 * <p>This method clears all text fields and labels in the cell.</p>
	 */
	public void reset() {
		userRatings.setText("");
		title.setText("");
		userName.setText("");
		createdDateLabel.setText("");
		reviewText.setText("");
	}
	
    @Override
    public void updateItem(MovieReviewDto item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setGraphic(null);
            reset();
        }
        else {
            setGraphic(box);
            set(item);
        }
        setAlignment(Pos.CENTER_LEFT);
    }
}