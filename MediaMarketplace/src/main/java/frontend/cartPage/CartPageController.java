package frontend.cartPage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.controllers.CartController;
import backend.controllers.OrderController;
import backend.dtos.CartDto;
import backend.dtos.CartProductDto;
import backend.dtos.references.CartProductReference;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.PurchaseOrderException;
import frontend.utils.AppUtils;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

@Component
public class CartPageController {
	
	public static final String PATH = "/frontend/cartPage/CartPage.fxml";
	
	@FXML
	VBox mainPane;
	
	@FXML
	private ListView<CartProductDto> cartItems;
	
	@FXML
	private Label emptyLabel;
	
	@FXML
	private Text itemsNumberText;
	
	@FXML
	private Text totalPriceText;
	
	@FXML
	private Button purchaseButton;
	
	@Autowired
	private CartController cartController;
	
	@Autowired
	private OrderController orderController;
	
	private ObservableList<CartProductDto> cartProducts;
	
	@FXML
	private void initialize() {
		this.cartProducts = FXCollections.observableArrayList();
		cartProducts.addListener(new InvalidationListener() {
			
			@Override
			public void invalidated(Observable observable) {
				if(cartProducts.isEmpty()) {
					cartItems.setVisible(false);
				}
				else {
					cartItems.setVisible(true);
				}
			}
		});
		if(cartProducts.isEmpty())
			cartItems.setVisible(false);
		emptyLabel.visibleProperty().bind(cartItems.visibleProperty().not());
		cartItems.setCellFactory(x -> new CartProductCell(this));
		cartItems.setItems(cartProducts);
		cartItems.setSelectionModel(null);
		refreshCart();
	}
	
	public void removeProductFromCart(CartProductDto cartProduct) {
		CartProductReference dto = new CartProductReference();
		dto.setProductId(cartProduct.getProduct().getId());
		try {
			cartController.removeProductFromCart(dto);
		} catch (EntityNotFoundException e1) {
			//don't need to change anything, maybe there is a glitch that the product is not in the cart, so removing from cart is necessary
			//but we will remove in case of a glitch, to avoid problems. 
		}
		cartProducts.remove(cartProduct);
		refreshCart();
	}
	
	private void refreshCart() {
		cartProducts.clear();
		try {
			CartDto cart = cartController.getCart();
			List<CartProductDto> resp = cart.getCartProducts();
			if(resp != null) {
				cartProducts.setAll(resp);
			}
			itemsNumberText.setText(""+cartProducts.size());
			totalPriceText.setText(""+cart.getTotalPrice());
		} catch (EntityNotFoundException e) {
			//if there is no cart for the user, then we will show him an empty cart
		}
	}
	
	@FXML
	private void purchaseCart() {
		Long orderId = null;
		try {
			orderId = orderController.placeOrder();
		} catch (PurchaseOrderException | EntityNotFoundException e) {
			//we will alert the user of the exception
			AppUtils.alertOfError("Problem with Purchasing Products", e.getMessage());
		}
		refreshCart();
		if(orderId != null) {
			AppUtils.alertOfInformation("Purchase is successfull", "Order number is: " + orderId);
		}
	}
}
