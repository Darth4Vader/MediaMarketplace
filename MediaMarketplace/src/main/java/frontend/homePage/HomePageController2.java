package frontend.homePage;

import java.net.MalformedURLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.controllers.ProductController;
import backend.entities.Product;
import frontend.AppUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;

@Component
public class HomePageController2 {
	
	public static final String PATH = "/frontend/homePage/HomePage.fxml";
	
	@FXML
	private GridPane productsPane;
	
	@FXML
	private ScrollPane movieScrollPane;
	
	@Autowired
	private ProductController productController;
	
	@FXML
	private void initialize() throws MalformedURLException {
		List<Product> products = productController.getAllProducts();
		AppUtils.loadProductsToGridPane(products, productsPane, movieScrollPane);
	}

}
