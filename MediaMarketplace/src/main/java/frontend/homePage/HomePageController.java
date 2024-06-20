package frontend.homePage;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.controllers.CartController;
import backend.controllers.MediaProductController;
import backend.dto.cart.AddProductToCartDto;
import backend.entities.Movie;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.services.TokenService;
import frontend.AppUtils;
import javafx.fxml.FXML;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

@Component
public class HomePageController {
	
	public static final String PATH = "/frontend/homePage/HomePage.fxml";
	
	@FXML
	private GridPane gridPane;
	
	@FXML
	private VBox mainPane;
	
	@Autowired
	private MediaProductController productController;
	
	@Autowired
	private CartController cartController;
	
	@Autowired
	private TokenService tokenService;

	@FXML
	private void initialize() throws MalformedURLException {
		final int cols = gridPane.getColumnCount();
		int row = 0;
		int currentCols = 0;
		String[] list = {"file:/C:/Users/itay5/git/MediaMarketplace/MediaMarketplace/posters/tt4154756.jpg",
				"file:/C:/Users/itay5/git/MediaMarketplace/MediaMarketplace/posters/tt4154796.jpg",
				"file:/C:/Users/itay5/git/MediaMarketplace/MediaMarketplace/posters/tt2395427.jpg",
				"file:/C:/Users/itay5/git/MediaMarketplace/MediaMarketplace/posters/tt0096895.jpg",
				"file:/C:/Users/itay5/git/MediaMarketplace/MediaMarketplace/posters/tt0103776.jpg",
				"file:/C:/Users/itay5/git/MediaMarketplace/MediaMarketplace/posters/tt0372784.jpg"};
		//for(RowConstraints cc : gridPane.getRowConstraints())
			//cc.prefHeightProperty().bind(mainPane.heightProperty().multiply(0.4));
		
		/*while(gridPane.getRowConstraints().size() > 0){
			gridPane.getRowConstraints().remove(0);
		}*/
		/*for(ColumnConstraints cc : gridPane.getColumnConstraints())
			cc.setPercentWidth(100/5);*/
			//cc.setPercentHeight(0.3);
		List<Movie> resp = productController.getAllMediaProducts();
		List<String> paths = new ArrayList<>();
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
			
			Button addToCart = new Button("Add to Cart");
			
			addToCart.setOnAction(e -> {
				AddProductToCartDto dto = new AddProductToCartDto();
				dto.setProductId(product.getId());
				try {
					cartController.addProductToCart(dto);
				} catch (EntityNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (EntityAlreadyExistsException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});
			
			b.setTop(addToCart);
			
			
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
		/*for(MediaProduct product : resp) {
			//String path = getClass().getResource(null);
			File file = new File(product.getImagePath());
			//System.out.println(file.getAbsolutePath());
			System.out.println(file.toURI().toURL().toExternalForm());
			ImageView view = new ImageView(file.toURI().toURL().toExternalForm());
			gridPane.add(view, currentCols, row);
			if(currentCols < cols)
				currentCols++;
			else {
				currentCols = 0;
				row++;
				break;
			}
		}*/
	}

}
