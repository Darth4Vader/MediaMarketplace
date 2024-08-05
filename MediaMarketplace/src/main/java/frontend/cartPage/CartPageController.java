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
import frontend.MovieRow;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;

@Component
public class CartPageController {
	
	public static final String PATH = "/frontend/cartPage/CartPage.fxml";
	
	@FXML
	private VBox mainPane;
	
	@FXML
	private ListView<CartProduct> cartItems;
	
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
	private CartProductRepository cartProductRepository;
	
	@Autowired
	private OrderController orderController;
	
	private ObservableList<CartProduct> cartProducts;
	
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
		/*cartProducts.addListener((ListChangeListener<CartProduct>) change -> {
			totalPriceText.setText(""+);
			while (change.next()) {
                if (change.wasAdded()) {
                    System.out.println(change.getAddedSubList().get(0)
                            + " was added to the list!");
                } else if (change.wasRemoved()) {
                    System.out.println(change.getRemoved().get(0)
                            + " was removed from the list!");
                }
            }
        });*/
		if(cartProducts.isEmpty())
			cartItems.setVisible(false);
		emptyLabel.visibleProperty().bind(cartItems.visibleProperty().not());
		cartItems.setCellFactory(new Callback<ListView<CartProduct>, ListCell<CartProduct>>() {
			
			@Override
			public ListCell<CartProduct> call(ListView<CartProduct> param) {
				// TODO Auto-generated method stub
				return new ListCell<>() {
					{
						setStyle("-fx-padding: 0px;");
					}
				    @Override
				    public void updateItem(CartProduct item, boolean empty) {
				        super.updateItem(item, empty);
				        if (item == null || empty) {
				        	System.out.println("Bye");
				            setGraphic(null);
				            setText(null);
				        }
				        else {
				            setGraphic(getCartProductPane(item));
				            setText(null);
				        }
				        setAlignment(Pos.CENTER_LEFT);
						setBorder(new Border(new BorderStroke(Color.PINK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
					            new BorderWidths(1))));
				        //setAlignment(Pos.CENTER);
				    }
				};
			}
		});
		cartItems.setItems(cartProducts);
		cartItems.setSelectionModel(null);
		refreshCart();
	}
	
	private HBox getCartProductPane(CartProduct cartProduct) {
		Product product = cartProduct.getProduct();
		Movie movie = product.getMovie();
		HBox mainBox = new HBox();
		HBox productBox = new HBox();
		HBox.setHgrow(productBox, Priority.ALWAYS);
		VBox imageBox = new VBox();
		ImageView view;
		try {
			view = AppUtils.loadImageViewFromClass(movie.getPosterPath());
			view.setPreserveRatio(true);
			view.fitWidthProperty().bind(mainPane.widthProperty().multiply(0.2));
			view.fitHeightProperty().bind(mainPane.heightProperty().multiply(0.4));
			imageBox.getChildren().add(view);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		VBox infoBox = new VBox();
		Label name = new Label(movie.getName());
		name.setWrapText(true);
		//b.prefWidthProperty().bind(mainPane.widthProperty());
		//b.maxWidthProperty().bind(mainPane.heightProperty().multiply(0.4));
		/*b.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
	            new BorderWidths(1))));*/
		
		Label type = new Label(cartProduct.isBuying() ? "Buy" : "Rent");
		type.setStyle("-fx-fill: green; -fx-font-size: 19");
		infoBox.getChildren().addAll(name, type);
		productBox.getChildren().addAll(imageBox, infoBox);
		Button removeFromCart = new Button("Delete");
		removeFromCart.setOnAction(e -> {
			CartProductDto dto = new CartProductDto();
			dto.setProductId(product.getId());
			try {
				cartController.removeProductFromCart(dto);
				cartProducts.remove(cartProduct);
				refreshTotalPrice();
			} catch (EntityNotFoundException e1) {
				//don't need to change anything, maybe there is a glitch that the product is not in the cart, so removing from cart is necessary
			}
		});
		double price = cartProduct.getPrice();
		Label priceText = new Label(""+price);
		priceText.setStyle("-fx-font-weight: bold; -fx-font-size: 19");
		mainBox.getChildren().addAll(productBox, priceText);
		return mainBox;
	}
	
	private void refreshCart() {
		cartProducts.clear();
		List<CartProduct> resp = cartController.getCartProducts();
		if(resp != null) {
			for(CartProduct cartProduct : resp) {
				cartProducts.add(cartProduct);
			}
		}
		refreshTotalPrice();
	}
	
	public void refreshTotalPrice() {
		double totalPrice = 0;
		itemsNumberText.setText(""+cartProducts.size());
		for(CartProduct cartProduct : cartProducts)
			totalPrice += cartProduct.getPrice();
		totalPriceText.setText(""+totalPrice);
	}
	
	@FXML
	private void purchaseCart() {
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
