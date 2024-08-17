package frontend.admin;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import backend.dtos.CreateMovieDto;
import backend.dtos.MovieDto;
import backend.dtos.references.MovieReference;
import backend.tmdb.CanUpdateException;
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
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Utility class for handling various admin page operations, including error alerts, 
 * cancellation notifications, and input validation.
 * <p>This class provides static methods to display alerts for exceptions, handle 
 * cancellation scenarios, and validate user input for movie-related data. It ensures 
 * that error messages are informative and cancellation messages are clear to the user.</p>
 */
public class AdminPagesUtils {

    /**
     * Displays an error alert when the media ID is not a valid number.
     * 
     * @param mediaId The media ID that caused the error.
     */
    public static void parseNumberException(String mediaId) {
        AppUtils.alertOfError("Movie Creation failed", "The movie media id (" + mediaId + ") is not a number");
    }
    
    /**
     * Displays an error alert with a message from a {@link NumberFormatException}.
     * 
     * @param exception The {@link NumberFormatException} containing the error message.
     */
    public static void parseNumberException(NumberFormatException exception) {
        AppUtils.alertOfError("Movie Creation failed", exception.getMessage());
    }

    /**
     * Displays an error alert for movie creation exceptions.
     * 
     * @param e The {@link CreateMovieException} to be reported.
     */
    public static void createMovieExceptionAlert(CreateMovieException e) {
        addMovieExceptionAlert(e, "Movie Creation exception");
    }
    
    /**
     * Displays an error alert for movie update exceptions.
     * 
     * @param e The {@link CreateMovieException} to be reported.
     */
    public static void updateMovieExceptionAlert(CreateMovieException e) {
        addMovieExceptionAlert(e, "Movie Update exception");
    }
    
    /**
     * Adds a movie-related exception alert with detailed information.
     * 
     * @param e The {@link CreateMovieException} containing the error details.
     * @param message The title message for the alert.
     */
    private static void addMovieExceptionAlert(CreateMovieException e, String message) {
        Alert alert = AppUtils.createAlertOfError(message, e.getMessage());
        VBox box = new VBox();
        box.setSpacing(5);
        List<NameAndException> list = e.getList();
        if (list != null) {
            for (NameAndException nameAndException : list) {
                HBox eBox = new HBox();
                eBox.setSpacing(10);
                Label nameLbl = new Label(nameAndException.getName());
                nameLbl.setStyle("-fx-font-weight: bold;");
                Label causeLbl = new Label(nameAndException.getException().getMessage());
                causeLbl.setTextFill(Color.RED);
                eBox.getChildren().addAll(nameLbl, causeLbl);
                box.getChildren().add(eBox);
            }
        }
        ScrollPane pane = new ScrollPane(box);
        alert.getDialogPane().setGraphic(pane);
        alert.show();
    }
    
    /**
     * Displays an alert when adding a movie is cancelled.
     * 
     * @param createMovieDto The {@link CreateMovieDto} associated with the cancellation.
     */
    public static void cancelAddMovieAlert(CreateMovieDto createMovieDto) {
        cancelChangeMovieAlert(false, getId(createMovieDto));
    }
    
    /**
     * Displays an alert when updating a movie is cancelled.
     * 
     * @param movieReference The {@link MovieReference} associated with the cancellation.
     */
    public static void cancelUpdateMovieAlert(MovieReference movieReference) {
        Long id = null;
        if (movieReference != null) {
            id = movieReference.getId();
        }
        cancelUpdateMovieAlert(id);
    }
    
    /**
     * Displays an alert when updating a movie is canceled due to an exception.
     * 
     * @param e The {@link CanUpdateException} that led to the cancellation.
     */
    public static void cancelUpdateMovieAlert(CanUpdateException e) {
        Long id = null;
        if (e != null) {
            id = getId(e.getCreateMovieDto());
        }
        cancelUpdateMovieAlert(id);
    }
    
    /**
     * Displays an alert when updating a movie is canceled.
     * 
     * @param id The ID of the movie that was being updated, or null if not applicable.
     */
    public static void cancelUpdateMovieAlert(Long id) {
        cancelChangeMovieAlert(true, id);
    }
    
    /**
     * Displays a cancellation alert for adding or updating a movie.
     * 
     * @param isUpdate True if the operation was an update; false if it was adding.
     * @param id The ID of the movie involved in the operation, or null if not applicable.
     */
    public static void cancelChangeMovieAlert(boolean isUpdate, Long id) {
        String strType = isUpdate ? "Update" : "Adding";
        Alert alert = AppUtils.createAlertOfInformation(strType + " Movie Cancelled", "");
        VBox box = new VBox();
        box.setSpacing(5);
        TextFlow line1 = new TextFlow();
        strType = isUpdate ? "updating" : "adding";
        line1.getChildren().add(new Text("The User chose to cancel " + strType + " the movie"));
        if (id != null) {
            Text l1w2 = new Text("" + id);
            l1w2.setStyle("-fx-font-weight: bold;");
            line1.getChildren().addAll(new Text(" with id \""), l1w2, new Text("\""));
        }
        TextFlow line2 = new TextFlow();
        Text l2w1 = new Text("Note");
        l2w1.setStyle("-fx-font-weight: bold;");
        Text l2w2 = new Text(" that there might have been some attributes that updated, "
                + "and some attributes that didn't update, due to the cancellation");
        line2.getChildren().addAll(l2w1, l2w2);
        box.getChildren().addAll(line1, line2);
        alert.getDialogPane().setContent(box);
        alert.show();
    }
    
    /**
     * Retrieves the ID from a {@link CreateMovieDto}.
     * 
     * @param createMovieDto The {@link CreateMovieDto} from which to extract the ID.
     * @return The ID of the movie, or null if not available.
     */
    private static Long getId(CreateMovieDto createMovieDto) {
        if (createMovieDto != null) {
            MovieDto dto = createMovieDto.getMovieDto();
            if (dto != null) {
                return dto.getId();
            }
        }
        return null;
    }
    
    /** The format of a regex of a number that can have "d" digits before the decimal mark, 
     * and at most 2 digits after the decimal mark */
    private static final String NUM_REGEX = "\\d{0,%d}(\\.\\d{0,2})?";
    
    /**
     * Creates a {@link ChangeListener} for text input control with a maximum number of characters.
     * 
     * @param control The {@link TextInputControl} to attach the listener to.
     * @param maxCharacters The maximum number of characters allowed.
     * @return A {@link ChangeListener} that enforces the character limit.
     */
    public static ChangeListener<String> textPropertyChangeListener(TextInputControl control, int maxCharacters) {
        return new ChangeListener<String>() {
            
            private final String regex = String.format(NUM_REGEX, maxCharacters);
            
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(newValue);
                if (!matcher.matches()) {
                    newValue = oldValue;
                }
                control.setText(newValue);
            }
        };
    }
    
    /**
     * Creates a {@link ChangeListener} for discount text input control.
     * <p>This listener enforces a maximum of two decimal places and ensures the value is not greater than 100.</p>
     * 
     * @param control The {@link TextInputControl} to attach the listener to.
     * @return A {@link ChangeListener} that enforces discount value constraints.
     */
    public static ChangeListener<String> discountTextListener(TextInputControl control) {
        return new ChangeListener<String>() {
            
            private final String regex = String.format(NUM_REGEX, 2);
            
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches(regex) && !newValue.matches("100([.]0{0,2})?")) {
                    newValue = oldValue;
                }
                control.setText(newValue);
            }
        };
    }
}