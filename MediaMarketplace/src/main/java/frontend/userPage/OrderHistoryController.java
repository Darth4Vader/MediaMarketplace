package frontend.userPage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.DataUtils;
import backend.controllers.OrderController;
import backend.entities.Movie;
import backend.entities.MoviePurchased;
import backend.entities.Order;
import frontend.App;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;

@Component
public class OrderHistoryController {
	
	public static final String PATH = "/frontend/userPage/OrderHistory.fxml";
	
	@FXML
	private ListView<Order> ordersPanel;
	
	@Autowired
	private OrderController orderController;
	
	@FXML
	private void initialize() {
		ordersPanel.setCellFactory(new Callback<ListView<Order>, ListCell<Order>>() {
			
			@Override
			public ListCell<Order> call(ListView<Order> param) {
				return new OrderCell();
			}
		});
		List<Order> orders = orderController.getUserOrders();
		ordersPanel.setItems(FXCollections.observableArrayList(orders));
		ordersPanel.setSelectionModel(null);
	}
	
	private class OrderCell extends ListCell<Order> {
		
		private VBox pane;
		private Text number;
		private Text purchasedDate;
		private VBox orderPanel;
		private Text price;
		
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
		
		private void set(Order order) {
			number.setText(""+order.getId());
			purchasedDate.setText(DataUtils.getLocalDateTimeInCurrentZone(order.getPurchasedDate()));
			orderPanel.getChildren().clear();
			List<MoviePurchased> orderItems = order.getPurchasedItems();
			for(MoviePurchased orderItem : orderItems) {
				System.out.println(order.getId() + " " + orderItem.getMovie().getName());
				BorderPane itemPane = getMoviePurchasedPane(orderItem);
				orderPanel.getChildren().add(itemPane);
			}
			price.setText(""+order.getTotalPrice());
		}
		
		private void reset() {
			orderPanel.getChildren().clear();
			number.setText("");
			purchasedDate.setText("");
			price.setText("");
		}
		
	    @Override
	    public void updateItem(Order item, boolean empty) {
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
			setBorder(new Border(new BorderStroke(Color.PINK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
		            new BorderWidths(1))));
	        //setAlignment(Pos.CENTER);
	    }
	}
	
	private BorderPane getMoviePurchasedPane(MoviePurchased moviePurchased) {
		BorderPane itemPane = new BorderPane();
		itemPane.setStyle("-fx-border-color: black");
		Movie movie = moviePurchased.getMovie();
		TextFlow nameFlow = new TextFlow();
		String nameStr = movie.getName();
		if(movie.getYear() != null)
			nameStr += " (" + movie.getYear() + ")";
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
		if(moviePurchased.isUseable()) {
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
