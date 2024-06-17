package frontend;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import backend.entities.MediaProduct;
import frontend.cartPage.CartPageController;
import frontend.homePage.HomePageController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

@Component
public class AppBarController {
	
	@FXML
	private TextField searchBar;
	
	@FXML
	private void enterCart() throws IOException {
		App.getApplicationInstance().changeAppPanel(CartPageController.PATH);
	}
	
	@FXML
	private void enterHome() throws IOException {
		App.getApplicationInstance().changeAppPanel(HomePageController.PATH);
	}
	
	@FXML
	private void search() throws IOException {
		String text = searchBar.getText();
		Map<String, List<?>> movie = SearchUtils.searchMoviesSort(text);
		List<?> movies = (List<?>) movie.get("Movies");
		VBox box = new VBox();
		for(Object obj : movies) {
			MediaProduct product = (MediaProduct) obj;
			ImageView view = AppUtils.loadImageFromClass(product.getImagePath());
			view.setPreserveRatio(true);
			//Button view = new Button();
			//view.maxWidth(Double.MAX_VALUE);
			//view.maxHeight(Double.MAX_VALUE);
			BorderPane b = new BorderPane();
			view.fitWidthProperty().bind(box.widthProperty());
			view.fitHeightProperty().bind(box.heightProperty().multiply(0.4));
			
			//view.fitWidthProperty().bind(gridPane.getColumnConstraints().get(currentCols).prefWidthProperty());
			//view.fitHeightProperty().bind(gridPane.getRowConstraints().get(row).prefHeightProperty());
			//b.setCenter(view);
			Label name = new Label(product.getMediaName());
			b.setBottom(name);
			box.getChildren().add(b);
			
		}
		App.getApplicationInstance().changeAppPanel(box);
	}

}