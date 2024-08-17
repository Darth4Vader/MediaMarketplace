package frontend.utils;

import backend.dtos.references.MovieReference;
import frontend.App;
import frontend.AppImageUtils;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;

/**
 * A custom {@code ImageView} that displays a movie poster and handles
 * interactions related to the movie.
 * <p>
 * This class extends {@code ImageView} to provide functionality for
 * setting a movie poster image, handling mouse click events, and resetting
 * the view.
 * </p>
 */
public class MovieImageView extends ImageView {

    /**
     * Constructs a {@code MovieImageView} with default properties.
     * <p>
     * The image view is initialized with ratio preservation enabled.
     * </p>
     */
	public MovieImageView() {
		this.setPreserveRatio(true);
	}
	
    /**
     * Sets the movie poster image and defines the behavior for mouse clicks.
     * <p>
     * If the {@code movie} is not {@code null}, this method sets the image
     * to the movie's poster and registers a click event handler that
     * navigates to the movie's page within the application. The cursor is
     * also set to a hand to indicate interactivity.
     * </p>
     * <p>
     * If the {@code movie} is {@code null}, this method calls
     * {@link #resetMovie()} to clear the image and reset the click event
     * handler and cursor.
     * </p>
     * 
     * @param movie the {@code MovieReference} containing movie information,
     *              or {@code null} to reset the image view
     */
	public void setMovie(MovieReference movie) {
		if(movie != null) {
			this.setImage(AppImageUtils.loadImageFromClass(movie.getPosterPath()));
			this.setOnMouseClicked(evt -> {
				App.getApplicationInstance().enterMoviePage(movie);
			});
			this.setCursor(Cursor.HAND);
		}
		else
			resetMovie();
	}
	
    /**
     * Resets the image view to its default state.
     * <p>
     * This method clears the image, removes any click event handlers, and
     * resets the cursor.
     * </p>
     */
	public void resetMovie() {
		this.setImage(null);
		this.setOnMouseClicked(null);
		this.setCursor(null);
	}
}