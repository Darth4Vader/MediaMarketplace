package frontend.cartPage;

import backend.dtos.CartProductDto;
import backend.dtos.ProductDto;
import backend.dtos.references.MovieReference;
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

/**
 * Custom ListCell for displaying a {@link CartProductDto} in a ListView.
 * <p>This class manages the layout and presentation of individual cart items,
 * including product details and a button to remove the item from the cart.</p>
 */
class CartProductCell extends ListCell<CartProductDto> {

    /**
     * Main container for the cart product cell layout.
     */
    private HBox mainBox;
    
    /**
     * View for displaying the product's image.
     */
    private MovieImageView view;
    
    /**
     * Label displaying the product's name.
     */
    private Label name;
    
    /**
     * Label indicating whether the product is for buying or renting.
     */
    private Label type;
    
    /**
     * Button for removing the product from the cart.
     */
    private Button removeFromCart;
    
    /**
     * Label showing the price of the product.
     */
    private Label priceText;
    
    /**
     * Controller that manages cart operations.
     */
    private CartPageController controller;
    
    /**
     * Constructs a {@code CartProductCell} with the specified controller.
     *
     * @param controller the controller used to handle cart operations
     */
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
    
    /**
     * Sets the content of the cell with the provided {@link CartProductDto}.
     *
     * @param cartProduct the cart product to display
     */
    public void set(CartProductDto cartProduct) {
        ProductDto productDto = cartProduct.getProduct();
        MovieReference movie = productDto.getMovie();
        view.setMovie(movie);
        name.setText(movie.getName());
        type.setText(cartProduct.isBuying() ? "Buy" : "Rent");
        removeFromCart.setOnAction(e -> {
            controller.removeProductFromCart(cartProduct);
        });
        double price = cartProduct.getPrice();
        priceText.setText(""+price);
    }
    
    /**
     * Resets the content of the cell.
     * <p>Clears the movie image, product details, and removes any actions associated with the cell.</p>
     */
    public void reset() {
        view.resetMovie();
        name.setText(null);
        type.setText(null);
        removeFromCart.setOnAction(null);
        priceText.setText(null);
    }
    
    @Override
    public void updateItem(CartProductDto item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setGraphic(null);
            reset();
        } else {
            setGraphic(mainBox);
            set(item);
        }
        setAlignment(Pos.CENTER_LEFT);
        setBorder(new Border(new BorderStroke(Color.PINK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
                new BorderWidths(1))));
    }
}