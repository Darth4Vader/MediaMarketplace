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

/**
 * Controller class for managing the Cart Page UI.
 * <p>This class handles the display and interaction with the user's shopping cart, including showing items,
 * updating totals, and processing purchases.</p>
 */
@Component
public class CartPageController {
    
    /**
     * Path to the FXML file for this controller.
     */
    public static final String PATH = "/frontend/cartPage/CartPage.fxml";
    
    /**
     * The main container pane for the cart page layout.
     */
    @FXML
    VBox mainPane;
    
    /**
     * ListView that displays the items in the user's cart.
     */
    @FXML
    private ListView<CartProductDto> cartItems;
    
    /**
     * Label that is displayed when the cart is empty.
     */
    @FXML
    private Label emptyLabel;
    
    /**
     * Text element that shows the number of items in the cart.
     */
    @FXML
    private Text itemsNumberText;
    
    /**
     * Text element that shows the total price of items in the cart.
     */
    @FXML
    private Text totalPriceText;
    
    /**
     * Button that triggers the purchase of the items in the cart.
     */
    @FXML
    private Button purchaseButton;
    
    /**
     * Controller for managing cart operations.
     */
    @Autowired
    private CartController cartController;
    
    /**
     * Controller for managing order operations.
     */
    @Autowired
    private OrderController orderController;
    
    /**
     * Observable list of products currently in the cart.
     */
    private ObservableList<CartProductDto> cartProducts;
    
    /**
     * Initializes the controller and sets up the cart page UI.
     * <p>Configures listeners for the cart products list and updates UI elements accordingly.</p>
     */
    @FXML
    private void initialize() {
        this.cartProducts = FXCollections.observableArrayList();
        cartProducts.addListener(new InvalidationListener() {
            
            @Override
            public void invalidated(Observable observable) {
                // Show or hide the cart items list based on whether it's empty
                cartItems.setVisible(!cartProducts.isEmpty());
            }
        });
        
        // Initially hide the cart items if the list is empty
        if (cartProducts.isEmpty()) {
            cartItems.setVisible(false);
        }
        
        // Bind visibility of the empty label to the cart items visibility
        emptyLabel.visibleProperty().bind(cartItems.visibleProperty().not());
        
        // Set up the ListView cell factory and items
        cartItems.setCellFactory(x -> new CartProductCell(this));
        cartItems.setItems(cartProducts);
        cartItems.setSelectionModel(null);
        
        refreshCart();
    }
    
    /**
     * Removes a product from the cart and updates the cart display.
     * 
     * @param cartProduct The product to remove from the cart.
     */
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
    
    /**
     * Refreshes the cart display by fetching the latest cart data and updating the UI.
     */
    private void refreshCart() {
        cartProducts.clear();
        try {
            CartDto cart = cartController.getCart();
            List<CartProductDto> resp = cart.getCartProducts();
            if (resp != null) {
                cartProducts.setAll(resp);
            }
            itemsNumberText.setText(String.valueOf(cartProducts.size()));
            totalPriceText.setText(String.valueOf(cart.getTotalPrice()));
        } catch (EntityNotFoundException e) {
			//if there is no cart for the user, then we will show him an empty cart
        }
    }
    
    /**
     * Handles the purchase of items in the cart.
     * <p>Attempts to place an order and shows a success or error message based on the result.</p>
     */
    @FXML
    private void purchaseCart() {
        Long orderId = null;
        try {
            orderId = orderController.placeOrder();
        } catch (PurchaseOrderException | EntityNotFoundException e) {
            // Alert the user if there is a problem with the purchase
            AppUtils.alertOfError("Problem with Purchasing Products", e.getMessage());
        }
        refreshCart();
        if (orderId != null) {
            // Show a success message with the order number if the purchase was successful
            AppUtils.alertOfInformation("Purchase is successfull", "Order number is: " + orderId);
        }
    }
}