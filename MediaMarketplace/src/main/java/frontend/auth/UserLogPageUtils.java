package frontend.auth;

import backend.DataUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;

/**
 * Utility class for managing user login and registration page functionalities.
 * <p>This class provides helper methods for monitoring text field changes and updating associated error labels.</p>
 */
public class UserLogPageUtils {
    
    /**
     * Creates a ChangeListener for monitoring changes in a text field.
     * <p>This listener clears the error label and resets the text field's border style when the user starts typing
     * into a text field that was initially empty.</p>
     *
     * @param textField The Node representing the text field to be monitored.
     * @param errorLabel The Label to be updated when the text field value changes.
     * @return A ChangeListener that responds to text field value changes.
     */
    public static ChangeListener<String> textFielsListener(Node textField, Label errorLabel) {
        return new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (DataUtils.isBlank(oldValue) && DataUtils.isNotBlank(newValue)) {
                    textField.setStyle(""); // Resets the style of the text field
                    if (!errorLabel.getText().isEmpty()) {
                        errorLabel.setText(""); // Clears the error label text
                    }
                }
            }
        };
    }
}