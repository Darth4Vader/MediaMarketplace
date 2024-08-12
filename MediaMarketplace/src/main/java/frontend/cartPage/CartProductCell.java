package frontend.cartPage;

import backend.entities.CartProduct;
import backend.entities.Movie;
import backend.entities.Product;
import frontend.utils.MovieImageView;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

class CartProductCell extends ListCell<CartProduct> {
	
	private HBox mainBox;
	private MovieImageView view;
	private Label name;
	private Label type;
	private Button removeFromCart;
	private Label priceText;
	private CartPageController controller;
	
	public CartProductCell(CartPageController controller) {
		this.controller = controller;
		setStyle("-fx-padding: 0px;");
		mainBox = new HBox();
		HBox productBox = new HBox();
		HBox.setHgrow(productBox, Priority.ALWAYS);
		VBox imageBox = new VBox();
		view = new MovieImageView();
		view.fitWidthProperty().bind(controller.mainPane.widthProperty().multiply(0.2));
		view.fitHeightProperty().bind(controller.mainPane.heightProperty().multiply(0.4));
		imageBox.getChildren().add(view);
		VBox infoBox = new VBox();
		name = new Label();
		name.setWrapText(true);
		type = new Label();
		type.setStyle("-fx-fill: green; -fx-font-size: 19");
		infoBox.getChildren().addAll(name, type);
		productBox.getChildren().addAll(imageBox, infoBox);
		removeFromCart = new Button("Delete");
		infoBox.getChildren().addAll(removeFromCart);
		priceText = new Label();
		priceText.setStyle("-fx-font-weight: bold; -fx-font-size: 19");
		mainBox.getChildren().addAll(productBox, priceText);
	}
	
	public void set(CartProduct cartProduct) {
		Product product = cartProduct.getProduct();
		Movie movie = product.getMovie();
		view.setMovie(movie);
		name.setText(movie.getName());
		type.setText(cartProduct.isBuying() ? "Buy" : "Rent");
		removeFromCart.setOnAction(e -> {
			controller.removeProductFromCart(cartProduct);
		});
		double price = cartProduct.getPrice();
		priceText.setText(""+price);
	}
	
	public void reset() {
		view.resetMovie();
		name.setText(null);
		type.setText(null);
		removeFromCart.setOnAction(null);
		priceText.setText(null);
	}
	
    @Override
    public void updateItem(CartProduct item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setGraphic(null);
            setText(null);
            reset();
        }
        else {
            setGraphic(mainBox);
            setText(null);
            set(item);
        }
        setAlignment(Pos.CENTER_LEFT);
		setBorder(new Border(new BorderStroke(Color.PINK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
	            new BorderWidths(1))));
    }
}
