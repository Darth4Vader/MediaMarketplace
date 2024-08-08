package frontend.moviePage;

import java.time.Duration;
import java.time.LocalDateTime;

import backend.entities.MovieReview;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class MoviePageUtils {

	public static TextFlow getUserRating(Text rating) {
		TextFlow textFlowPane = new TextFlow();
		Text star = new Text("★");
		star.setFill(Color.BLUE);
		star.setFont(Font.font(star.getFont().getSize()+3));
		rating.setStyle("-fx-font-weight: bold; -fx-font-size: 19");
		Text rangeRating = new Text("/100");
		textFlowPane.getChildren().addAll(star, rating, rangeRating);
		return textFlowPane;
	}
	
	public static TextFlow getUserRating(MovieReview review) {
		return getUserRating(new Text(" "+review.getRating()));
	}
	
	
	public static Duration getRemainTime(LocalDateTime timeSince) {
		LocalDateTime now = LocalDateTime.now();
		try {
			return Duration.between(now, timeSince);
		}
		catch (Exception e) {
			return null;
		}
	}
	
}
