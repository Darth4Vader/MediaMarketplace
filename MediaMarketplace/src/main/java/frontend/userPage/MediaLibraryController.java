package frontend.userPage;

import java.net.MalformedURLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.controllers.MediaPurchasedController;
import backend.entities.Movie;
import frontend.AppUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

@Component
public class MediaLibraryController {
	
	public static final String PATH = "/frontend/userPage/MediaLibrary.fxml";
	
	@FXML
	private GridPane gridPane;
	
	@FXML
	private VBox mainPane;
	
	@Autowired
	private MediaPurchasedController mediaPurchasedController;

	@FXML
	private void initialize() throws MalformedURLException {
		final int cols = gridPane.getColumnCount();
		int row = 0;
		int currentCols = 0;
		List<Movie> resp = mediaPurchasedController.getAllActiveMediaProductsOfUser();
		for(Movie product : resp) {
			//paths.add(new File(product.getImagePath()).toURI().toURL().toExternalForm());
		//}
		//list = paths.toArray(new String[paths.size()]);
		//for(String path : list) {
			ImageView view = AppUtils.loadImageFromClass(product.getImagePath());
			view.setPreserveRatio(true);
			//Button view = new Button();
			//view.maxWidth(Double.MAX_VALUE);
			//view.maxHeight(Double.MAX_VALUE);
			BorderPane b = new BorderPane();
			view.fitWidthProperty().bind(mainPane.widthProperty());
			view.fitHeightProperty().bind(mainPane.heightProperty().multiply(0.4));
			
			//view.fitWidthProperty().bind(gridPane.getColumnConstraints().get(currentCols).prefWidthProperty());
			//view.fitHeightProperty().bind(gridPane.getRowConstraints().get(row).prefHeightProperty());
			b.setCenter(view);
			Label name = new Label(product.getName());
			b.setBottom(name);
			//b.prefWidthProperty().bind(mainPane.widthProperty());
			//b.maxWidthProperty().bind(mainPane.heightProperty().multiply(0.4));
			b.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
		            new BorderWidths(1))));
			gridPane.add(b, currentCols, row);
			System.out.println(currentCols + " " + cols);
			System.out.println("("+currentCols+","+row+")");
			if(currentCols < cols-1)
				currentCols++;
			else {
				//RowConstraints rowConst = gridPane.getRowConstraints().get(row);
				//System.out.println(rowConst);
				currentCols = 0;
				row++;
				//RowConstraints rowConst = new RowConstraints();
				
				//break;
			}
		}
	}
	
	
}
