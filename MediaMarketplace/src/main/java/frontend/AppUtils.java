package frontend;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import backend.dto.cart.CartProductDto;
import backend.entities.Movie;
import backend.entities.Product;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import frontend.help.MoviePageController;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

public class AppUtils {

	public AppUtils() {
		// TODO Auto-generated constructor stub
	}
	
	public static Image loadImageFromClass(String path) throws MalformedURLException {
		URL url;
		try {
			url = new URL(path);
		}
		catch (MalformedURLException e) {
			url = new File(path).toURI().toURL();
		}
		
		String fullPath = url.toExternalForm();
		System.out.println(fullPath);
		return new Image(fullPath, true);
	}
	
	public static ImageView loadImageViewFromClass(String path) throws MalformedURLException {
		return new ImageView(loadImageFromClass(path));
	}
	
	/*public static Pane getMoviePane(Movie movie) throws MalformedURLException, IOException {
		ImageView view = AppUtils.loadImageViewFromClass(movie.getPosterPath());
		view.setPreserveRatio(true);
		BorderPane b = new BorderPane();
		view.fitWidthProperty().bind(b.widthProperty());
		view.fitHeightProperty().bind(b.heightProperty());
		b.setCenter(view);
		Label name = new Label(movie.getName());
		b.setBottom(name);
		b.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
	            new BorderWidths(1))));
		b.setOnMouseClicked(evt -> {
			FXMLLoader loader = App.getApplicationInstance().getFXMLLoader(MoviePageController.PATH);
			try {
				Parent root = loader.load();
				MoviePageController controller = loader.getController();
				controller.initializeMovie(movie);
				enterPanel(root);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		return b;
	}*/
	
	public static BorderPane getMoviePane(Movie movie, Region sizePane) {
		BorderPane b = new BorderPane();
		ImageView view = null;
		try {
			view = loadImageViewFromClass(movie.getPosterPath());
		} catch (MalformedURLException e) {
		}
		if(view != null) {
			view.setPreserveRatio(true);
			view.fitWidthProperty().bind(sizePane.widthProperty().multiply(0.2));
			view.fitHeightProperty().bind(sizePane.heightProperty().multiply(0.4));
			b.setCenter(view);
		}
		Label name = new Label(movie.getName());
		name.setWrapText(true);
		b.setBottom(name);
		b.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
	            new BorderWidths(1))));
		b.setOnMouseClicked(evt -> {
			App.getApplicationInstance().enterMoviePage(movie);
		});
		return b;
	}
	
	public static void loadProductsToGridPane(List<Product> products, GridPane moviePane, Region sizePane) {
		final int cols = moviePane.getColumnCount();
		int row = 0;
		int currentCols = 0;
		moviePane.getChildren().clear();
		for(Product product : products) {
			Movie movie = product.getMovie();
			BorderPane pane = getMoviePane(movie, sizePane);
			moviePane.add(pane, currentCols, row);
			if(currentCols < cols-1)
				currentCols++;
			else {
				currentCols = 0;
				row++;
			}
		}
	}
	
	public static void loadMoviesToGridPane(List<Movie> movies, GridPane moviePane, Region sizePane) {
		final int cols = moviePane.getColumnCount();
		int row = 0;
		int currentCols = 0;
		moviePane.getChildren().clear();
		for(Movie movie : movies) {
			BorderPane pane = getMoviePane(movie, sizePane);
			moviePane.add(pane, currentCols, row);
			if(currentCols < cols-1)
				currentCols++;
			else {
				currentCols = 0;
				row++;
			}
		}
	}
	
	public static void enterPanel(Node node) throws IOException {
		App.getApplicationInstance().changeAppPanel(node);
	}
	
	public static void addToGridPane(GridPane gridPane, Node comp) {
		int size = gridPane.getChildren().size();
		int r = size / 3;
		int c = size - r*3;
		gridPane.add(comp, c, r);
	}

}
