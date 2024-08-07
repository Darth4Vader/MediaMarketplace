package frontend;

import java.net.MalformedURLException;

import backend.entities.Movie;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;

public class MovieImageView extends ImageView {

	public MovieImageView() {
		this.setPreserveRatio(true);
	}
	
	public void setMovie(Movie movie) {
		if(movie != null) {
			this.setImage(AppUtils.loadImageFromClass(movie.getPosterPath()));
			this.setOnMouseClicked(evt -> {
				App.getApplicationInstance().enterMoviePage(movie);
			});
			this.setCursor(Cursor.HAND);
		}
		else
			resetMovie();
	}
	
	public void resetMovie() {
		this.setImage(null);
		this.setOnMouseClicked(null);
		this.setCursor(null);
	}

}
