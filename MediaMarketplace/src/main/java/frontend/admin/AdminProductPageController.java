package frontend.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.DataUtils;
import backend.controllers.ProductController;
import backend.controllers.UserAuthenticateController;
import backend.dto.mediaProduct.ProductDto;
import backend.entities.Movie;
import backend.entities.Product;
import backend.exceptions.EntityNotFoundException;
import backend.tmdb.CreateMovie;
import backend.tmdb.CreateMovieException;
import frontend.admin.createMovieLogView.CreateMovieLoggerControl;
import frontend.utils.AppUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

@Component
public class AdminProductPageController {
	
	public static final String PATH = "/frontend/admin/AdminProductPage.fxml";
	
	@FXML
	private Label statusLabel;
	
	@FXML
	private TextField rentPriceField;
	
	@FXML
	private TextField buyPriceField;
	
	@FXML
	private TextField rentDiscountField;
	
	@FXML
	private TextField buyDiscountField;
	
	@Autowired
	private ProductController productController;
	
	@Autowired
	private CreateMovie createMovie;
	
	@Autowired
	private UserAuthenticateController userAuthenticateController;
	
	private CreateMovieLoggerControl createMovieLoggerControl;
	
	private Movie movie;
	private Product product;
	
	@FXML
	private void initialize() {
		//first check that the current user is an admin to enter this page
		userAuthenticateController.checkIfCurrentUserIsAdmin();
		buyPriceField.textProperty().addListener(AdminPagesUtils.textPropertyChangeListener(buyPriceField, 3));
		buyDiscountField.textProperty().addListener(AdminPagesUtils.textPropertyChangeListener(buyDiscountField, 3));
		rentPriceField.textProperty().addListener(AdminPagesUtils.discountTextListener(rentPriceField));
		rentDiscountField.textProperty().addListener(AdminPagesUtils.discountTextListener(rentDiscountField));
		this.createMovieLoggerControl = new CreateMovieLoggerControl(createMovie);
	}
	
	public void initializeProduct(Movie movie) {
		this.movie = movie;
		initializeProduct();
	}
	
	private void initializeProduct() {
		if(this.movie != null) {
			setProduct();
			refreshProduct();
		}
	}
	
	private void setProduct() {
		if(this.movie != null) try {
			product = productController.getProductByMovieId(movie.getId());
		} catch (EntityNotFoundException e) {
			//this is okay, it means that the movie is not a product (Not available for sale)
			product = null;
		}
	}
	
	private void refreshProduct() {
		if(product != null) {
			statusLabel.setTextFill(Color.GREEN);
			statusLabel.setText("The Movie is a Product");
			buyPriceField.setText(""+product.getBuyPrice());
			rentPriceField.setText(""+product.getRentPrice());
			buyDiscountField.setText(""+product.getBuyDiscount());
			rentDiscountField.setText(""+product.getRentDiscount());
		}
		else {
			statusLabel.setTextFill(Color.RED);
			statusLabel.setText("The Movie is not a Product");
		}
	}
	
	@FXML
	private void updateMovie() {
		String mediaId = movie.getMediaID();
		createMovieLoggerControl.start();
		try {
			createMovie.updateMovieInDatabase(Integer.parseInt(movie.getMediaID()));
			createMovieLoggerControl.finishedTask();
		} catch (NumberFormatException e1) {
			createMovieLoggerControl.close();
			AdminPagesUtils.parseNumberException(mediaId);
		} catch (CreateMovieException e) {
			createMovieLoggerControl.close();
			//when the movie update fails, we will alert the user of the reasons
			AdminPagesUtils.updateMovieExceptionAlert(e);
		} catch (EntityNotFoundException e) {
			createMovieLoggerControl.close();
			//can ignore, because we create the genres, so the exception will not be triggered.
			//and the movie is created, therefore it will be in the database, and the exception will not be triggered
			AppUtils.alertOfError("Update Movie Error", e.getMessage());
		}
	}
		
	
	@FXML
	private void updateProductPrice() {
		//we create the new prices to update
		ProductDto productDto = new ProductDto();
		productDto.setBuyPrice(DataUtils.getNumber(buyPriceField.getText()));
		productDto.setRentPrice(DataUtils.getNumber(rentPriceField.getText()));
		productDto.setBuyDiscount(DataUtils.getNumber(buyDiscountField.getText()));
		productDto.setRentDiscount(DataUtils.getNumber(rentDiscountField.getText()));
		//if there is a product set
		if(product != null) {
			productDto.setProductId(product.getId());
			try {
				productController.updateProduct(productDto);
			} catch (EntityNotFoundException e) {
				//this will not be activated because we check that the product exists, but we will add message in case
				AppUtils.alertOfError("Update Product Error", e.getMessage());
			}
		}
		else if(this.movie != null) {
			try {
				productDto.setMovieId(this.movie.getId());
				productController.addProduct(productDto);
			} catch (EntityNotFoundException e) {
				//this will not be activated because we check that the movie exists, but we will add message in case
				AppUtils.alertOfError("Create Product Error", e.getMessage());
			}
		}
		//we refresh the product
		initializeProduct();
	}

}
