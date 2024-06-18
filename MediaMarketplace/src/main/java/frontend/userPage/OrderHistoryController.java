package frontend.userPage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.controllers.OrderController;
import backend.entities.MediaProduct;
import backend.entities.MediaPurchased;
import backend.entities.Order;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

@Component
public class OrderHistoryController {
	
	public static final String PATH = "/frontend/userPage/OrderHistory.fxml";
	
	@FXML
	private VBox ordersPanel;
	
	@Autowired
	private OrderController orderController;
	
	@FXML
	private void initialize() {
		List<Order> orders = orderController.getUserOrders();
		System.out.println(orders);
		for(Order order : orders) {
			BorderPane pane = new BorderPane();
			Label lbl = new Label("Order Number: " + order.getId());
			VBox orderPanel = new VBox();
			List<MediaPurchased> orderItems = order.getPurchasedItems();
			for(MediaPurchased orderItem : orderItems) {
				BorderPane itemPane = new BorderPane();
				MediaProduct product = orderItem.getMediaProduct();
				Label itemName = new Label(product.getMediaName());
				Label priceLbl = new Label(""+orderItem.getPurchasePrice());
				itemPane.setLeft(itemName);
				itemPane.setRight(priceLbl);
				Insets val = new Insets(5);
				BorderPane.setMargin(itemName, val);
				BorderPane.setMargin(priceLbl, val);
				orderPanel.getChildren().add(itemPane);
			}
			pane.setTop(lbl);
			pane.setCenter(orderPanel);
			pane.setStyle("-fx-border-color: #C9FFFE");
			ordersPanel.getChildren().add(pane);
		}
	}
	
}
