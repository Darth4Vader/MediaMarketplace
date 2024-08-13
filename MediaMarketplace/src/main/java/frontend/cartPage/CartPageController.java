package frontend.cartPage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.controllers.CartController;
import backend.controllers.OrderController;
import backend.dto.cart.CartDto;
import backend.dto.cart.CartProductDto;
import backend.dto.cart.CartProductReference;
import backend.entities.CartProduct;
import backend.entities.Order;
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
		CartDto cart = cartController.getCart();
		List<CartProductDto> resp = cart.getCartProducts();
		if(resp != null) {
			cartProducts.setAll(resp);
		}
		double totalPrice = 0;
		itemsNumberText.setText(""+cartProducts.size());
		totalPriceText.setText(""+cart.getTotalPrice());
	}	
	
	/*private void refreshCart() {
		cartProducts.clear();
		List<CartProduct> resp = cartController.getCartProducts();
		if(resp != null) {
			cartProducts.setAll(resp);
		}
		refreshTotalPrice();
	}*/
	
	/*public void refreshTotalPrice() {
		double totalPrice = 0;
		itemsNumberText.setText(""+cartProducts.size());
		for(CartProduct cartProduct : cartProducts)
			totalPrice += cartProduct.getPrice();
		totalPriceText.setText(""+totalPrice);
	}*/
	
	@FXML
	private void purchaseCart() {
		Order order = null;
		try {
			order = orderController.placeOrder();
		} catch (PurchaseOrderException e) {
			//we will alert the user for the exception reasons.
			AppUtils.alertOfError("Problem with Purchasing Products", e.getMessage());
		}
		refreshCart();
		if(order != null) {
			AppUtils.alertOfInformation("Purchase is successfull", "Order number is: " + order.getId());
		}
	}
}
