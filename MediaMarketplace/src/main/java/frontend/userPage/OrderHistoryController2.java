package frontend.userPage;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.DataUtils;
import backend.controllers.OrderController;
import backend.entities.Movie;
import backend.entities.MoviePurchased;
import backend.entities.Order;
import frontend.App;
import frontend.AppUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

@Component
public class OrderHistoryController2 {
	
	public static final String PATH = "/frontend/userPage/OrderHistory.fxml";
	
	@FXML
	private VBox ordersPanel;
	
	@Autowired
	private OrderController orderController;
	
	@FXML
	private void initialize() {
		System.out.println("koraaaaaaa");
		List<Order> orders = orderController.getUserOrders();
		System.out.println(orders);
		for(Order order : orders) {
			BorderPane pane = new BorderPane();
			TextFlow orderDescription = new TextFlow();
			orderDescription.getChildren().add(new Text("Order Number: "));
			Text number = new Text(""+order.getId());
			number.setStyle("-fx-font-weight: bold; -fx-font-size: 19");
			orderDescription.getChildren().addAll(number, new Text(" - Date: " + DataUtils.getLocalDateTimeInCurrentZone(order.getPurchasedDate())));
			//Label lbl = new Label("Order Number: " + order.getId());
			VBox orderPanel = new VBox();
			orderPanel.setStyle("-fx-border-color: #525453");
			List<MoviePurchased> orderItems = order.getPurchasedItems();
			for(MoviePurchased orderItem : orderItems) {
				BorderPane itemPane = new BorderPane();
				itemPane.setStyle("-fx-border-color: black");
				Movie movie = orderItem.getMovie();
				TextFlow nameFlow = new TextFlow();
				String nameStr = movie.getName();
				if(movie.getYear() != null)
					nameStr += " (" + movie.getYear() + ")";
				Text name = new Text(nameStr);
				name.setCursor(Cursor.HAND);
				name.setOnMouseClicked(event -> {
					App.getApplicationInstance().enterMoviePage(orderItem.getMovie());
				});
				nameFlow.getChildren().addAll(name, new Text(" - "));
				Text type = new Text();
				type.setText(orderItem.isRented() ? "Rented" : "Purchased");
				type.setStyle("-fx-font-weight: bold; -fx-font-size: 19");
				nameFlow.getChildren().addAll(type, new Text(" ("));
				Text use = new Text();
				if(orderItem.isUseable()) {
					use.setText("Useable");
					use.setStyle("-fx-fill: green");
				}
				else {
					use.setText("Expired");
					use.setStyle("-fx-fill: red");
				}
				nameFlow.getChildren().addAll(use, new Text(")"));
				Label priceLbl = new Label(""+orderItem.getPurchasePrice());
				itemPane.setLeft(nameFlow);
				itemPane.setRight(priceLbl);
				Insets val = new Insets(5);
				BorderPane.setMargin(nameFlow, val);
				BorderPane.setMargin(priceLbl, val);
				orderPanel.getChildren().add(itemPane);
			}
			pane.setTop(orderDescription);
			pane.setCenter(orderPanel);
			TextFlow totalPrice = new TextFlow();
			totalPrice.getChildren().add(new Text("Total Price: "));
			Text price = new Text(""+order.getTotalPrice());
			price.setStyle("-fx-font-weight: bold; -fx-font-size: 19");
			totalPrice.getChildren().add(price);
			pane.setBottom(totalPrice);
			pane.setStyle("-fx-border-color: #C9FFFE");
			ordersPanel.getChildren().add(pane);
		}
	}
	
}
