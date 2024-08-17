package frontend.moviePage;

import java.time.Duration;
import java.time.LocalDateTime;

import backend.dtos.references.MovieReviewReference;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Utility class for various functions related to the movie page.
 * <p>This class provides static methods for formatting and calculating movie-related information.</p>
 */
public class MoviePageUtils {

	/**
	 * Creates a {@link TextFlow} containing a formatted user rating.
	 * <p>The rating is displayed as a blue star followed by the rating value and a "/100" range.</p>
	 * 
	 * @param rating A {@link Text} object representing the rating value.
	 * @return A {@link TextFlow} containing the styled user rating.
	 */
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
	
	/**
	 * Creates a {@link TextFlow} containing a formatted user rating from a {@link MovieReviewReference}.
	 * <p>The rating is displayed as a blue star followed by the rating value from the review and a "/100" range.</p>
	 * 
	 * @param review A {@link MovieReviewReference} object containing the rating.
	 * @return A {@link TextFlow} containing the styled user rating.
	 */
	public static TextFlow getUserRating(MovieReviewReference review) {
		return getUserRating(new Text(" "+review.getRating()));
	}
	
	/**
	 * Calculates the remaining time between the current time and a specified time.
	 * <p>This method returns the duration remaining from the current time to the specified time, 
	 * or {@code null} if an exception occurs.</p>
	 * 
	 * @param timeSince A {@link LocalDateTime} object representing the time since which the remaining time is calculated.
	 * @return A {@link Duration} representing the remaining time, or {@code null} if the calculation fails.
	 */
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