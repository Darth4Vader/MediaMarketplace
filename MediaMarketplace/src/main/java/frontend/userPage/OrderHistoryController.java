package frontend.userPage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.DataUtils;
import backend.controllers.OrderController;
import backend.dtos.MoviePurchasedDto;
import backend.dtos.OrderDto;
import backend.dtos.references.MovieReference;
import frontend.App;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;

/**
 * Controller for the Order History page, which displays a list of orders made by the user.
 */
@Component
public class OrderHistoryController {
	
    /**
     * The FXML path for the Order History view.
     */
    public static final String PATH = "/frontend/userPage/OrderHistory.fxml";

    /**
     * The ListView displaying the user's orders.
     */
    @FXML
    private ListView<OrderDto> ordersPanel;

    /**
     * Controller for managing user orders.
     */
    @Autowired
    private OrderController orderController;

    /**
     * Initializes the order history view by loading the user's orders.
     */
	@FXML
	private void initialize() {
		ordersPanel.setCellFactory(new Callback<ListView<OrderDto>, ListCell<OrderDto>>() {
			
			@Override
			public ListCell<OrderDto> call(ListView<OrderDto> param) {
				return new OrderCell();
			}
		});
		List<OrderDto> orders = orderController.getUserOrders();
		ordersPanel.setItems(FXCollections.observableArrayList(orders));
		ordersPanel.setSelectionModel(null);
	}
	
    /**
     * Class representing a custom ListCell for displaying individual orders.
     */
	private class OrderCell extends ListCell<OrderDto> {
		
	    /**
	     * The container for the entire cell layout, including order details and total price.
	     */
	    private VBox pane;

	    /**
	     * The text element displaying the order number.
	     */
	    private Text number;

	    /**
	     * The text element displaying the date the order was purchased.
	     */
	    private Text purchasedDate;

	    /**
	     * The container for displaying the list of movies included in the order.
	     */
	    private VBox orderPanel;

	    /**
	     * The text element displaying the total price of the order.
	     */
	    private Text price;
		
        /**
         * Constructs an OrderCell with a custom layout for displaying order details.
         */
		public OrderCell(){
			setStyle("-fx-padding: 0px;");
			pane = new VBox();
			TextFlow orderDescription = new TextFlow();
			orderDescription.getChildren().add(new Text("Order Number: "));
			number = new Text();
			number.setStyle("-fx-font-weight: bold; -fx-font-size: 19");
			purchasedDate = new Text();
			orderDescription.getChildren().addAll(number, new Text(" - Date: "), purchasedDate);
			orderPanel = new VBox();
			orderPanel.setStyle("-fx-border-color: #525453");
			TextFlow totalPrice = new TextFlow();
			totalPrice.getChildren().add(new Text("Total Price: "));
			price = new Text();
			price.setStyle("-fx-font-weight: bold; -fx-font-size: 19");
			totalPrice.getChildren().add(price);
			pane.setStyle("-fx-border-color: #C9FFFE");
			pane.getChildren().addAll(orderDescription, orderPanel, totalPrice);
		}
		
        /**
         * Sets the order details in the cell.
         * 
         * @param order The order to display.
         */
		private void set(OrderDto order) {
			number.setText(""+order.getId());
			purchasedDate.setText(DataUtils.getLocalDateTimeInCurrentZone(order.getPurchasedDate()));
			orderPanel.getChildren().clear();
			List<MoviePurchasedDto> orderItems = order.getPurchasedItems();
			for(MoviePurchasedDto orderItem : orderItems) {
				BorderPane itemPane = getMoviePurchasedPane(orderItem);
				orderPanel.getChildren().add(itemPane);
			}
			price.setText(""+order.getTotalPrice());
		}
		
        /**
         * Resets the cell's content to empty.
         */
		private void reset() {
			orderPanel.getChildren().clear();
			number.setText("");
			purchasedDate.setText("");
			price.setText("");
		}
		
        /**
         * Updates the display of the cell based on the provided order.
         * 
         * @param item The order to display.
         * @param empty Whether the cell is empty.
         */
	    @Override
	    public void updateItem(OrderDto item, boolean empty) {
	        super.updateItem(item, empty);
	        if (item == null || empty) {
	            setGraphic(null);
	            reset();
	        }
	        else {
	            set(item);
	            setGraphic(pane);
	        }
	        setAlignment(Pos.CENTER_LEFT);
	    }
	}
	
    /**
     * Creates a BorderPane to display the details of a purchased movie.
     * 
     * @param moviePurchased The movie purchased details to display.
     * @return A BorderPane containing the movie details.
     */
	private BorderPane getMoviePurchasedPane(MoviePurchasedDto moviePurchased) {
		BorderPane itemPane = new BorderPane();
		itemPane.setStyle("-fx-border-color: black");
		MovieReference movie = moviePurchased.getMovie();
		TextFlow nameFlow = new TextFlow();
		String nameStr = movie.getName();
		Text name = new Text(nameStr);
		name.setCursor(Cursor.HAND);
		name.setOnMouseClicked(event -> {
			App.getApplicationInstance().enterMoviePage(moviePurchased.getMovie());
		});
		nameFlow.getChildren().addAll(name, new Text(" - "));
		Text type = new Text();
		type.setText(moviePurchased.isRented() ? "Rented" : "Purchased");
		type.setStyle("-fx-font-weight: bold; -fx-font-size: 19");
		nameFlow.getChildren().addAll(type, new Text(" ("));
		Text use = new Text();
		if(DataUtils.isUseable(moviePurchased)) {
			use.setText("Useable");
			use.setStyle("-fx-fill: green");
		}
		else {
			use.setText("Expired");
			use.setStyle("-fx-fill: red");
		}
		nameFlow.getChildren().addAll(use, new Text(")"));
		Label priceLbl = new Label(""+moviePurchased.getPurchasePrice());
		itemPane.setLeft(nameFlow);
		itemPane.setRight(priceLbl);
		Insets val = new Insets(5);
		BorderPane.setMargin(nameFlow, val);
		BorderPane.setMargin(priceLbl, val);
		return itemPane;
	}
}