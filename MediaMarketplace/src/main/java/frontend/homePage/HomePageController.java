package frontend.homePage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.controllers.ProductController;
import backend.entities.Product;
import frontend.AppUtils;
import frontend.MovieRow;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

@Component
public class HomePageController {
	
	public static final String PATH = "/frontend/homePage/HomePage.fxml";
	
	@FXML
	private ListView<MovieRow> productsPane;
	
	@Autowired
	private ProductController productController;
	
	@FXML
	private void initialize() {		
		List<Product> products = productController.getAllProducts();
		AppUtils.FullListViewAsGridPage(productsPane, products);
	}

}