package frontend;

import java.io.File;
import java.net.MalformedURLException;

import backend.dto.cart.CartProductDto;
import backend.entities.Movie;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class AppUtils {

	public AppUtils() {
		// TODO Auto-generated constructor stub
	}
	
	public static ImageView loadImageFromClass(String path) throws MalformedURLException {
		String fullPath = new File(path).toURI().toURL().toExternalForm();
		return new ImageView(fullPath);
	}
	
	public static Pane getMoviePane(Movie movie) throws MalformedURLException {
		ImageView view = AppUtils.loadImageFromClass(movie.getImagePath());
		view.setPreserveRatio(true);
		BorderPane b = new BorderPane();
		view.fitWidthProperty().bind(b.widthProperty());
		view.fitHeightProperty().bind(b.heightProperty());
		b.setCenter(view);
		Label name = new Label(movie.getName());
		b.setBottom(name);
		b.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
	            new BorderWidths(1))));
		return b;
	}
	
	public static void addToGridPane(GridPane gridPane, Node comp) {
		int size = gridPane.getChildren().size();
		int r = size / 3;
		int c = size - r*3;
		gridPane.add(comp, c, r);
	}

}
