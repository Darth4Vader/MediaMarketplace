package frontend.cartPage;

import java.net.MalformedURLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.controllers.CartController;
import backend.controllers.OrderController;
import backend.dto.cart.CartProductDto;
import backend.entities.CartProduct;
import backend.entities.Movie;
import backend.entities.MoviePurchased;
import backend.entities.Order;
import backend.entities.Product;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.PurchaseOrderException;
import backend.repositories.CartProductRepository;
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
	
	@FXML
	private Button purchaseButton;
	
	@Autowired
	private CartController cartController;
	
	@Autowired
	private CartProductRepository cartProductRepository;
	
	@Autowired
	private OrderController orderController;
	
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
		refreshCart();
	}
	
	private void refreshCart() throws MalformedURLException {
		cartItems.getChildren().clear();
		List<CartProduct> resp = cartController.getCartProducts();
		for(CartProduct cartProduct : resp) {
			Product product = cartProduct.getProduct();
			Movie movie = product.getMovie();
			ImageView view = AppUtils.loadImageFromClass(movie.getImagePath());
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
			Label name = new Label(movie.getName());
			b.setBottom(name);
			//b.prefWidthProperty().bind(mainPane.widthProperty());
			//b.maxWidthProperty().bind(mainPane.heightProperty().multiply(0.4));
			b.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
		            new BorderWidths(1))));
			
			Button removeFromCart = new Button("Remove From Cart");
			removeFromCart.setOnAction(e -> {
				CartProductDto dto = new CartProductDto();
				dto.setProductId(product.getId());
				try {
					cartController.removeProductFromCart(dto);
					cartItems.getChildren().remove(b);
					List<CartProduct> listt = cartProductRepository.findAll();
					for(CartProduct car : listt) {
						//System.out.println(car.getCart().getUser().getUsername() + " " + car.getProduct().getName());
					}
				} catch (EntityNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});
			b.setTop(removeFromCart);
			cartItems.getChildren().add(b);
		}
	}
	
	@FXML
	private void purchaseCart() throws MalformedURLException {
		try {
			orderController.placeOrder();
		} catch (PurchaseOrderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		refreshCart();
		List<Order> orders = orderController.getUserOrders();
		for(Order order : orders) {
			System.out.println(order.getUser().getUsername());
			System.out.println(order.getTotalPrice());
			System.out.println(order.getId());
			for(MoviePurchased purchase : order.getPurchasedItems()) {
				System.out.println(purchase.getMovie().getName());
			}
		}
	}
}
