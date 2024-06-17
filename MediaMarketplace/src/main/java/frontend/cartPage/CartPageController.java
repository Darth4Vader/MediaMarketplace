package frontend.cartPage;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.controllers.CartController;
import backend.dto.cart.AddProductToCartDto;
import backend.entities.CartProduct;
import backend.entities.MediaProduct;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import frontend.AppUtils;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

@Component
public class CartPageController {
	
	public static final String PATH = "/frontend/cartPage/CartPage.fxml";
	
	@FXML
	private VBox mainPane;
	
	@FXML
	private VBox cartItems;
	
	@FXML
	private Label emptyLabel;
	
	@Autowired
	private CartController cartController;
	
	@FXML
	private void initialize() throws MalformedURLException {
		ObservableList<Node> list = cartItems.getChildren();
		list.addListener(new InvalidationListener() {
			
			@Override
			public void invalidated(Observable observable) {
				if(list.isEmpty()) {
					cartItems.setVisible(false);
				}
				else {
					cartItems.setVisible(true);
				}
			}
		});
		if(list.isEmpty())
			cartItems.setVisible(false);
		emptyLabel.visibleProperty().bind(cartItems.visibleProperty().not());
		List<CartProduct> resp = cartController.getCartProducts();
		for(CartProduct cartProduct : resp) {
			MediaProduct product = cartProduct.getProduct();
			ImageView view = AppUtils.loadImageFromClass(product.getImagePath());
			view.setPreserveRatio(true);
			//Button view = new Button();
			//view.maxWidth(Double.MAX_VALUE);
			//view.maxHeight(Double.MAX_VALUE);
			BorderPane b = new BorderPane();
			//view.fitWidthProperty().bind(mainPane.widthProperty());
			view.fitHeightProperty().bind(mainPane.heightProperty().multiply(0.4));
			
			//view.fitWidthProperty().bind(gridPane.getColumnConstraints().get(currentCols).prefWidthProperty());
			//view.fitHeightProperty().bind(gridPane.getRowConstraints().get(row).prefHeightProperty());
			b.setCenter(view);
			Label name = new Label(product.getMediaName());
			b.setBottom(name);
			//b.prefWidthProperty().bind(mainPane.widthProperty());
			//b.maxWidthProperty().bind(mainPane.heightProperty().multiply(0.4));
			b.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
		            new BorderWidths(1))));
			
			Button removeFromCart = new Button("Remove From Cart");
			removeFromCart.setOnAction(e -> {
				AddProductToCartDto dto = new AddProductToCartDto();
				dto.setProductId(product.getId());
				try {
					cartController.removeProductFromCart(dto);
					cartItems.getChildren().remove(b);
				} catch (EntityNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});
			b.setTop(removeFromCart);
			cartItems.getChildren().add(b);
		}
	}
}