package frontend.homePage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.controllers.ProductController;
import backend.dtos.ProductDto;
import frontend.utils.AppUtils;
import frontend.utils.MovieRow;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

/**
 * Controller for the Home Page of the application.
 * <p>This controller manages the display of a list of products on the home page,
 * including fetching product data and setting up the UI components to show the products.</p>
 */
@Component
public class HomePageController {

    /**
     * Path to the FXML file for the Home Page.
     */
    public static final String PATH = "/frontend/homePage/HomePage.fxml";
    
    /**
     * ListView component for displaying products as rows.
     */
    @FXML
    private ListView<MovieRow> productsPane;
    
    /**
     * Controller for managing product-related operations.
     */
    @Autowired
    private ProductController productController;
    
    /**
     * Initializes the Home Page by fetching all products and displaying them.
     * <p>This method is called after the FXML file has been loaded and it populates
     * the {@link ListView} with product data retrieved from the {@link ProductController}.</p>
     */
    @FXML
    private void initialize() {        
        List<ProductDto> products = productController.getAllProducts();
        AppUtils.FullListViewAsGridPage(productsPane, products);
    }
}