package frontend.admin;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import backend.tmdb.CreateMovieException;
import backend.tmdb.NameAndException;
import frontend.utils.AppUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class AdminPagesUtils {
	
	/**
	 * this can happen when the media id is not a number <br>
	 * we will add an error message
	 * @param mediaId
	 */
	public static void parseNumberException(String mediaId) {
		AppUtils.alertOfError("Movie Creation failed", "The movie media id (" + mediaId + ") is not a number");
	}
	
	public static void parseNumberException(NumberFormatException exception) {
		AppUtils.alertOfError("Movie Creation failed", exception.getMessage());
	}


	public static void createMovieExceptionAlert(CreateMovieException e) {
		addMovieExceptionAlert(e, "Movie Creation exception");
	}
	
	public static void updateMovieExceptionAlert(CreateMovieException e) {
		addMovieExceptionAlert(e, "Movie Update exception");
	}
	
	private static void addMovieExceptionAlert(CreateMovieException e, String message) {
		Alert alert = AppUtils.createAlertOfError(message, e.getMessage());
		VBox box = new VBox();
		box.setSpacing(5);
		List<NameAndException> list = e.getList();
		if(list != null) for(NameAndException nameAndException : list) {
			HBox eBox = new HBox();
			eBox.setSpacing(10);
			Label nameLbl = new Label(nameAndException.getName());
			nameLbl.setStyle("-fx-font-weight: bold;");
			Label causeLbl = new Label(nameAndException.getException().getMessage());
			causeLbl.setTextFill(Color.RED);
			eBox.getChildren().addAll(nameLbl, causeLbl);
			box.getChildren().add(eBox);
		}
		ScrollPane pane = new ScrollPane(box);
        alert.getDialogPane().setGraphic(pane);
        alert.show();
	}
	
	private static final String NUM_REGEX = "\\d{0,%d}(\\.\\d{0,2})?";
	
	public static ChangeListener<String> textPropertyChangeListener(TextInputControl control, int maxCharacters) {
		return new ChangeListener<String>() {
			
			private final String regex = String.format(NUM_REGEX, maxCharacters);
			
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		    	Pattern pattern = Pattern.compile(regex);
		    	Matcher matcher = pattern.matcher(newValue);
		        if (!matcher.matches()) {
		        	newValue = oldValue;
		        }
		        control.setText(newValue);
		    }
	    };
	}
	
	public static ChangeListener<String> discountTextListener(TextInputControl control) {
		return new ChangeListener<String>() {
			
			private final String regex = String.format(NUM_REGEX, 2);
			
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		        if (!newValue.matches(regex) && !newValue.matches("100([.]0{0,2})?")) {
		        	newValue = oldValue;
		        }
		        control.setText(newValue);
		    }
	    };
	}
	
}
