package frontend.admin;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.DataUtils;
import backend.controllers.ProductController;
import backend.controllers.UserAuthenticateController;
import backend.dtos.references.MovieReference;
import backend.dtos.references.ProductReference;
import backend.exceptions.EntityNotFoundException;
import backend.tmdb.CreateMovie;
import backend.tmdb.CreateMovieException;
import frontend.App;
import frontend.admin.createMovieLogView.CreateMovieLoggerControl;
import frontend.utils.AppUtils;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

/**
 * Controller for the Admin Product Page, which manages the display and update
 * of movie-related product information such as prices and discounts.
 * <p>This component interacts with the backend to retrieve and update product
 * information, and provides a user interface to allow administrators to manage
 * product details associated with movies.</p>
 */
@Component
public class AdminProductPageController {

    /** The path to the FXML file for this controller. */
    public static final String PATH = "/frontend/admin/AdminProductPage.fxml";

    /**
     * Label displaying the status of the product information.
     * <p>This label is used to inform the user whether the movie is currently
     * associated with a product or not.</p>
     */
    @FXML
    private Label statusLabel;

    /**
     * Text field for inputting or displaying the rental price of the product.
     * <p>This field allows the administrator to set or view the rental price of
     * the movie if it is associated with a product.</p>
     */
    @FXML
    private TextField rentPriceField;

    /**
     * Text field for inputting or displaying the purchase price of the product.
     * <p>This field allows the administrator to set or view the purchase price of
     * the movie if it is associated with a product.</p>
     */
    @FXML
    private TextField buyPriceField;

    /**
     * Text field for inputting or displaying the rental discount of the product.
     * <p>This field allows the administrator to set or view the rental discount for
     * the movie if it is associated with a product.</p>
     */
    @FXML
    private TextField rentDiscountField;

    /**
     * Text field for inputting or displaying the purchase discount of the product.
     * <p>This field allows the administrator to set or view the purchase discount for
     * the movie if it is associated with a product.</p>
     */
    @FXML
    private TextField buyDiscountField;

    /**
     * Controller for managing product-related operations in the backend.
     * <p>This instance is used to interact with the backend to perform operations such
     * as retrieving and updating product information.</p>
     */
    @Autowired
    private ProductController productController;

    /**
     * Service for creating and managing movie-related data in the backend.
     * <p>This instance is used for updating movie details and handling movie-related
     * operations.</p>
     */
    @Autowired
    private CreateMovie createMovie;

    /**
     * Controller for managing user authentication and authorization.
     * <p>This instance is used to check if the current user has administrative privileges
     * to access the admin product page.</p>
     */
    @Autowired
    private UserAuthenticateController userAuthenticateController;

    /**
     * Custom logger control for displaying and managing log messages during movie
     * update operations.
     * <p>This instance is used to provide feedback to the user about the status of movie
     * updates through a custom logging interface.</p>
     */
    private CreateMovieLoggerControl createMovieLoggerControl;

    /**
     * Reference to the movie currently being managed.
     * <p>This instance holds the details of the movie for which product information
     * is being displayed or updated.</p>
     */
    private MovieReference movie;

    /**
     * Reference to the product associated with the movie.
     * <p>This instance holds the details of the product, including pricing and discount
     * information, if the movie is associated with a product.</p>
     */
    private ProductReference product;

    /**
     * Initializes the controller. This method checks if the current user is an admin
     * and sets up listeners for the text fields to validate input.
     */
    @FXML
    private void initialize() {
        // First, check that the current user is an admin to enter this page
        userAuthenticateController.checkIfCurrentUserIsAdmin();
        buyPriceField.textProperty().addListener(AdminPagesUtils.textPropertyChangeListener(buyPriceField, 3));
        buyDiscountField.textProperty().addListener(AdminPagesUtils.textPropertyChangeListener(buyDiscountField, 3));
        rentPriceField.textProperty().addListener(AdminPagesUtils.discountTextListener(rentPriceField));
        rentDiscountField.textProperty().addListener(AdminPagesUtils.discountTextListener(rentDiscountField));
        this.createMovieLoggerControl = new CreateMovieLoggerControl(createMovie);
    }

    /**
     * Initializes the product information based on the provided movie reference.
     * 
     * @param movie The {@link MovieReference} associated with the product.
     */
    public void initializeProduct(MovieReference movie) {
        this.movie = movie;
        initializeProduct();
    }

    /**
     * Initializes and refreshes the product details.
     * <p>This method is called to set up the product information if a movie reference
     * is provided. It fetches the product details from the backend and updates the UI.</p>
     */
    private void initializeProduct() {
        if (this.movie != null) {
            setProduct();
            refreshProduct();
        }
    }

    /**
     * Fetches and sets the product information based on the current movie reference.
     * <p>This method queries the backend to get the product information associated with
     * the movie. If the movie is not associated with a product, it sets the product to null.</p>
     */
    private void setProduct() {
        if (this.movie != null) {
            try {
                product = productController.getProductReferenceOfMovie(movie.getId());
            } catch (EntityNotFoundException e) {
                // The movie is not a product (Not available for sale)
                product = null;
            }
        }
    }

    /**
     * Refreshes the UI with the product information.
     * <p>This method updates the status label and sets the text fields with the product's
     * price and discount information. It also handles the case where the movie is not
     * associated with a product.</p>
     */
    private void refreshProduct() {
        if (product != null) {
            statusLabel.setTextFill(Color.GREEN);
            statusLabel.setText("The Movie is a Product");
            setNumberTextField(buyPriceField, "" + product.getBuyPrice());
            setNumberTextField(rentPriceField, "" + product.getRentPrice());
            setNumberTextField(buyDiscountField, product.getBuyDiscount());
            setNumberTextField(rentDiscountField, product.getRentDiscount());
        } else {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("The Movie is not a Product");
        }
    }

    /**
     * Sets the text of a text field to a formatted number string.
     * 
     * @param textField The {@link TextField} to update.
     * @param bigDecimal The {@link BigDecimal} value to format and set.
     */
    private void setNumberTextField(TextField textField, BigDecimal bigDecimal) {
        String numText = (bigDecimal == null) ? "0.00" : bigDecimal.toString();
        setNumberTextField(textField, numText);
    }

    /**
     * Sets the text of a text field to a specified string.
     * <p>Formats the string to ensure that the text field displays a valid number.</p>
     * 
     * @param textField The {@link TextField} to update.
     * @param str The string value to set in the text field.
     */
    private void setNumberTextField(TextField textField, String str) {
        String numText = DataUtils.isBlank(str) ? "0.00" : str;
        textField.setText(numText);
    }

    /**
     * Updates the movie information in the database.
     * <p>This method creates a background task to update the movie in the database
     * and handles success, failure, and cancellation scenarios.</p>
     */
    @FXML
    private void updateMovie() {
        // First, we will create a task in order to push the custom logger message to ListView by updating JavaFX
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                createMovie.updateMovieInDatabase(movie);
                return null;
            }
        };

        task.setOnFailed(v -> {
            // We will handle every possible exception of the method that we call
            createMovieLoggerControl.close();
            Throwable exception = task.getException();
            if (exception instanceof NumberFormatException) {
                // If the mediaId is not a number
                AdminPagesUtils.parseNumberException((NumberFormatException) exception);
            } else if (exception instanceof CreateMovieException) {
                // When the movie update fails, we will alert the user of the reasons
                AdminPagesUtils.updateMovieExceptionAlert((CreateMovieException) exception);
            } else if (exception instanceof EntityNotFoundException) {
                // Can ignore, because we create the genres, so the exception will not be triggered.
                // And the movie is created, therefore it will not be triggered.
                AppUtils.alertOfError("Update Movie Error", exception.getMessage());
            } else {
                // We will throw every possible exception
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), exception);
            }
        });
        // We will add a successful notify id the method finish
        task.setOnSucceeded(v -> {
            createMovieLoggerControl.finishedTask();
            App.getApplicationInstance().enterMoviePage(movie);
        });
        // If the task is cancelled, before the method finish, then we notify the user of the consequences
        task.setOnCancelled(v -> {
            createMovieLoggerControl.close();
            AdminPagesUtils.cancelUpdateMovieAlert(movie);
        });
        // We will open the custom view with the task
        createMovieLoggerControl.start(task);
        // And then start the task as a new thread, in order to synchronize with JavaFX
        new Thread(task).start();
    }

    /**
     * Updates the product prices and discounts in the backend.
     * <p>This method checks if the product exists, updates it if it does, or creates
     * a new product if it does not. It also refreshes the product information after
     * the update or creation.</p>
     */
    @FXML
    private void updateProductPrice() {
        ProductReference productDto = new ProductReference();
        productDto.setId(product != null ? product.getId() : null);
        productDto.setBuyPrice(DataUtils.getNumber(buyPriceField.getText()));
        productDto.setRentPrice(DataUtils.getNumber(rentPriceField.getText()));
        productDto.setBuyDiscount(DataUtils.getBigDecimal(buyDiscountField.getText()));
        productDto.setRentDiscount(DataUtils.getBigDecimal(rentDiscountField.getText()));

        if (product != null) {
            try {
                productController.updateProduct(productDto);
            } catch (EntityNotFoundException e) {
                AppUtils.alertOfError("Update Product Error", e.getMessage());
            }
        } else if (this.movie != null) {
            try {
                productDto.setMovieId(this.movie.getId());
                productController.addProduct(productDto);
            } catch (EntityNotFoundException e) {
                AppUtils.alertOfError("Create Product Error", e.getMessage());
            }
        }

        initializeProduct();
    }
}