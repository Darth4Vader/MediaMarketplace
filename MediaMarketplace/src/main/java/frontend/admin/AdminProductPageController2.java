package frontend.admin;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.DataUtils;
import backend.controllers.ProductController;
import backend.dto.mediaProduct.ProductDto;
import backend.entities.Movie;
import backend.entities.Product;
import backend.exceptions.EntityNotFoundException;
import backend.tmdb.CreateMovie;
import frontend.AppUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.NumberStringConverter;

@Component
public class AdminProductPageController2 {
	
	public static final String PATH = "/frontend/admin/AdminProductPage.fxml";
	
	@FXML
	private ImageView posterView;
	
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
	
	@FXML
	private void initialize() {
		//DecimalFormat format = new DecimalFormat("00.00");
		//rentPriceField.setTextFormatter(new TextFormatter<>(format));
		buyPriceField.textProperty().addListener(textPropertyChangeListener(buyPriceField, 3));
		buyDiscountField.textProperty().addListener(textPropertyChangeListener(buyDiscountField, 3));
		
		rentPriceField.textProperty().addListener(discountTextListener(rentPriceField));
		rentDiscountField.textProperty().addListener(discountTextListener(rentDiscountField));
		
		/*percentNumField(buyDiscountField, DISCOUNT_REGEX);
		percentNumField(rentDiscountField, DISCOUNT_REGEX);*/
		
		/*percentNumField(buyDiscountField, DISCOUNT_REGEX);
		percentNumField(rentDiscountField, DISCOUNT_REGEX);*/
	}
	
	public static ChangeListener<String> textPropertyChangeListener(TextInputControl control, int maxCharacters) {
		return new ChangeListener<String>() {
			
			private final String regex = String.format(NUM_REGEX, maxCharacters);
			
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		    	System.out.println(newValue);
		    	//String text = control.getText()
		    	System.out.println(regex);
		    	Pattern pattern = Pattern.compile(regex);
		    	Matcher matcher = pattern.matcher(newValue);
		        if (matcher.matches()) {
		        	System.out.println(matcher.group());
		        		/*"-?\\d{0,%d}(\\.\\d{0,2})?")) {*/
		        	//newValue = newValue.replaceAll("[^\\d]", "");
		        	//newValue = newValue.replaceAll(".", "");
		        }
		        else {
			    	/*Pattern pattern2 = Pattern.compile("("+regex+")$");
			    	Matcher matcher2 = pattern2.matcher(newValue);
			    	if(matcher2.find()) {
			    		newValue = matcher2.group();
			    	}
			        else {*/
			        	System.out.println("No2");
			        	newValue = oldValue;
			        //}
		        }
		        /*if(newValue.matches("(\\d*(\\.\\d{0,2})?)$")) {
		        	//newValue.matches("-?\\d*(\\.\\d{0,2})?")
		        	System.out.println(matcher.find());
		        	System.out.println(matcher.groupCount());
		        	System.out.println(matcher.group(0));
		        	//newValue = matcher.group();
		        	System.out.println("New " + newValue);
			    	Pattern pattern2 = Pattern.compile("(\\d{0,3}(\\.\\d{0,2})?)$");
			    			/*("(\\d*(\\.\\d{0,2})?)$");*/
			    	/*Matcher matcher2 = pattern2.matcher(newValue);
			    	while(matcher2.find()) {
			    		System.out.print(matcher2.group() + ",");
			    	}
			    	System.out.println();
		        	System.out.println("No");
		        }
		        else {
		        	System.out.println("No2");
		        	newValue = oldValue;
		        }*/
	        	/*if(newValue.length() > maxCharacters)
	        		newValue = newValue.substring(0, maxCharacters);*/
		        control.setText(newValue);
		    }
	    };
	}
	
	private ChangeListener<String> discountTextListener(TextInputControl control) {
		return new ChangeListener<String>() {
			
			private final String regex = String.format(NUM_REGEX, 2);
			
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		    	System.out.println(newValue);
		    	//String text = control.getText()
		    	System.out.println(regex);
		    	Pattern pattern = Pattern.compile(regex);
		    	Matcher matcher = pattern.matcher(newValue);
		        if (newValue.matches(regex) || newValue.matches("100([.]0{0,2})?")) {
		        	//System.out.println(matcher.group());
		        		/*"-?\\d{0,%d}(\\.\\d{0,2})?")) {*/
		        	//newValue = newValue.replaceAll("[^\\d]", "");
		        	//newValue = newValue.replaceAll(".", "");
		        }
		        else {
			    	/*Pattern pattern2 = Pattern.compile("("+regex+")$");
			    	Matcher matcher2 = pattern2.matcher(newValue);
			    	if(matcher2.find()) {
			    		newValue = matcher2.group();
			    	}
			        else {*/
			        	System.out.println("No2");
			        	newValue = oldValue;
			        //}
		        }
		        /*if(newValue.matches("(\\d*(\\.\\d{0,2})?)$")) {
		        	//newValue.matches("-?\\d*(\\.\\d{0,2})?")
		        	System.out.println(matcher.find());
		        	System.out.println(matcher.groupCount());
		        	System.out.println(matcher.group(0));
		        	//newValue = matcher.group();
		        	System.out.println("New " + newValue);
			    	Pattern pattern2 = Pattern.compile("(\\d{0,3}(\\.\\d{0,2})?)$");
			    			/*("(\\d*(\\.\\d{0,2})?)$");*/
			    	/*Matcher matcher2 = pattern2.matcher(newValue);
			    	while(matcher2.find()) {
			    		System.out.print(matcher2.group() + ",");
			    	}
			    	System.out.println();
		        	System.out.println("No");
		        }
		        else {
		        	System.out.println("No2");
		        	newValue = oldValue;
		        }*/
	        	/*if(newValue.length() > maxCharacters)
	        		newValue = newValue.substring(0, maxCharacters);*/
		        control.setText(newValue);
		    }
	    };
	}
	
	private static final String NUM_REGEX = "\\d{0,%d}(\\.\\d{0,2})?";
	
	private void percentNumField(TextField field, String regex) {
		UnaryOperator<Change> integerFilter = change -> {
		    String newText = change.getControlNewText();
		    if (newText.matches(regex) || newText.matches("100([.]0{0,2})?")) {
		        System.out.println(newText.matches(regex));
		    	return change;
		    }
	        System.out.println(newText.matches(regex));
		    return null;
		};

		field.setTextFormatter(
		    new TextFormatter<BigDecimal>(new BigDecimalStringConverter(), null, integerFilter));
	}
	
	private Movie movie;
	
	private Product product;
	
	public void initializeProduct(Movie movie) {
		this.movie = movie;
		posterView.setImage(AppUtils.loadImageFromClass(movie.getPosterPath()));
		try {
			product = productController.getProductByMovieId(movie.getId());
			/*statusLabel.setTextFill(Color.GREEN);
			statusLabel.setText("The Movie is a Product");*/
		} catch (EntityNotFoundException e) {
			product = null;
			/*statusLabel.setTextFill(Color.RED);
			statusLabel.setText("The Movie is not a Product");*/
		}
		refreshProduct();
		/*if(product != null) {
			buyPriceField.setText(""+product.getBuyPrice());
			rentPriceField.setText(""+product.getRentPrice());
			buyDiscountField.setText(""+product.getBuyDiscount());
			rentDiscountField.setText(""+product.getRentDiscount());
		}*/
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
	private void updateProduct() {
		ProductDto productDto = new ProductDto();
		productDto.setBuyPrice(DataUtils.getNumber(buyPriceField.getText()));
		productDto.setRentPrice(DataUtils.getNumber(rentPriceField.getText()));
		productDto.setBuyDiscount(DataUtils.getNumber(buyDiscountField.getText()));
		productDto.setRentDiscount(DataUtils.getNumber(rentDiscountField.getText()));
		if(product != null) {
			productDto.setProductId(product.getId());
			try {
				productController.updateProduct(productDto);
				product = productController.getProductByMovieId(movie.getId());
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
		}
		else {
			try {
				Long id = productController.addProduct(productDto);
				System.out.println(id);
				product = productController.getProductByMovieId(movie.getId());
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
		}
		refreshProduct();
	}

}
