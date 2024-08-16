package frontend.moviePage;

import java.time.LocalDateTime;

import backend.DataUtils;
import backend.dtos.MovieReviewDto;
import backend.dtos.references.MovieReviewReference;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

class MovieReviewCell extends ListCell<MovieReviewDto> {
	
	private VBox box;
	private Text userRatings;
	private Text title;
	private Label userName;
	private Label createdDateLabel;
	private Label reviewText;
	
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
            setText(null);
            reset();
        }
        else {
            setGraphic(box);
            setText(null);
            set(item);
        }
        setAlignment(Pos.CENTER_LEFT);
		setBorder(new Border(new BorderStroke(Color.PINK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
	            new BorderWidths(1))));
    }
}
