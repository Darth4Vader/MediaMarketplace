package frontend.admin;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.DataUtils;
import backend.controllers.ProductController;
import backend.controllers.UserAuthenticateController;
import backend.dto.mediaProduct.MovieReference;
import backend.dto.mediaProduct.ProductReference;
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
	
	private MovieReference movie;
	private ProductReference product;
	
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
	
	public void initializeProduct(MovieReference movie) {
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
			product = productController.getProductReferenceOfMovie(movie.getId());
		} catch (EntityNotFoundException e) {
			//this is okay, it means that the movie is not a product (Not available for sale)
			product = null;
		}
	}
	
	private void refreshProduct() {
		if(product != null) {
			statusLabel.setTextFill(Color.GREEN);
			statusLabel.setText("The Movie is a Product");
			setNumberTextField(buyPriceField,  ""+product.getBuyPrice());
			setNumberTextField(rentPriceField, ""+product.getRentPrice());
			setNumberTextField(buyDiscountField, product.getBuyDiscount());
			setNumberTextField(rentDiscountField, product.getRentDiscount());
		}
		else {
			statusLabel.setTextFill(Color.RED);
			statusLabel.setText("The Movie is not a Product");
		}
	}
	
	private void setNumberTextField(TextField textField, BigDecimal bigDecimal) {
		String numText;
		if(bigDecimal == null)
			numText = "0.00";
		else
			 numText = ""+bigDecimal;
		setNumberTextField(textField, numText);
	}
	
	private void setNumberTextField(TextField textField, String str) {
		String numText;
		if(DataUtils.isBlank(str))
			numText = "0.00";
		else
			 numText = str;
		textField.setText(numText);
	}
	
	@FXML
	private void updateMovie() {
		createMovieLoggerControl.start();
		try {
			createMovie.updateMovieInDatabase(movie);
			createMovieLoggerControl.finishedTask();
		} catch (NumberFormatException e1) {
			createMovieLoggerControl.close();
			AdminPagesUtils.parseNumberException(e1);
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
		ProductReference productDto = new ProductReference();
		productDto.setId(product.getId());
		productDto.setBuyPrice(DataUtils.getNumber(buyPriceField.getText()));
		productDto.setRentPrice(DataUtils.getNumber(rentPriceField.getText()));
		productDto.setBuyDiscount(DataUtils.getBigDecimal(buyDiscountField.getText()));
		productDto.setRentDiscount(DataUtils.getBigDecimal(rentDiscountField.getText()));
		//if there is a product set
		if(product != null) {
			productDto.setId(product.getId());
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
