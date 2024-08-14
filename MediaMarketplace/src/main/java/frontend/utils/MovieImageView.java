package frontend.utils;

import backend.dto.mediaProduct.MovieReference;
import frontend.App;
import frontend.AppImageUtils;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;

public class MovieImageView extends ImageView {

	public MovieImageView() {
		this.setPreserveRatio(true);
	}
	
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
	
	public void resetMovie() {
		this.setImage(null);
		this.setOnMouseClicked(null);
		this.setCursor(null);
	}

}
