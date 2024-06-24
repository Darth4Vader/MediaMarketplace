package frontend;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import backend.entities.Movie;
import frontend.cartPage.CartPageController;
import frontend.homePage.HomePageController;
import frontend.userPage.UserPageController;
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
	private void enterUserPage() throws IOException {
		App.getApplicationInstance().changeAppPanel(UserPageController.PATH);
	}
	
	@FXML
	private void search() throws IOException {
		String text = searchBar.getText();
		List<Movie> movies = SearchUtils.searchMoviesSort(new SortDto(text));
		VBox box = new VBox();
		for(Movie product : movies) {
			ImageView view = AppUtils.loadImageViewFromClass(product.getPosterPath());
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
			Label name = new Label(product.getName());
			b.setBottom(name);
			box.getChildren().add(b);
			
		}
		App.getApplicationInstance().changeAppPanel(box);
	}

}
