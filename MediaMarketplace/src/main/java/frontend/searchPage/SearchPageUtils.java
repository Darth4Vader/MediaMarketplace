package frontend.searchPage;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextInputControl;
import backend.DataUtils;

/**
 * Utility class containing methods for managing text input validation
 * and formatting in the search page.
 */
public class SearchPageUtils {

    /**
     * Creates a {@link ChangeListener} for a {@link TextInputControl} that enforces a maximum number of characters
     * and ensures that only numeric values are allowed.
     * 
     * <p>This listener will automatically format the input to contain only digits and will truncate
     * the input to the specified maximum number of characters.</p>
     * 
     * @param control The {@link TextInputControl} to apply the listener to.
     * @param maxCharacters The maximum number of characters allowed in the input.
     * @return A {@link ChangeListener} that enforces the numeric input and character limit.
     */
    public static ChangeListener<String> textPropertyChangeListener(TextInputControl control, int maxCharacters) {
        return new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*"))
                    newValue = newValue.replaceAll("[^\\d]", "");
                if (newValue.length() > maxCharacters)
                    newValue = newValue.substring(0, maxCharacters);
                control.setText(newValue);
            }
        };
    }

    /**
     * Creates a {@link ChangeListener} for a {@link TextInputControl} that formats rating input values.
     * 
     * <p>This listener enforces that the input contains only numeric values, ensures that ratings are 
     * within the range of 0 to 100, and removes leading zeros. The formatted input will be displayed 
     * in the control.</p>
     * 
     * @param control The {@link TextInputControl} to apply the listener to.
     * @return A {@link ChangeListener} that formats rating input values.
     */
    public static ChangeListener<String> ratingChangeListener(TextInputControl control) {
        return new ChangeListener<String>() {
            private String prev;

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (DataUtils.equalsIgnoreCase(prev, newValue))
                    return;
                if (!newValue.matches("\\d*"))
                    newValue = newValue.replaceAll("[^\\d]", "");
                if (newValue.startsWith("00") && newValue.length() == 3) {
                    newValue = newValue.substring(1, 3);
                } else if (!newValue.equals("0") && !newValue.equals("00")) {
                    newValue = newValue.replaceFirst("^0+(?!$)", "");
                    if (newValue.length() > 3) {
                        newValue = newValue.substring(0, 3);
                    }
                    Integer num = DataUtils.getIntegerNumber(newValue);
                    if (num != null) {
                        if (num > 100 && newValue.length() >= 3) {
                            if (oldValue != null && oldValue.length() < 3)
                                newValue = newValue.substring(0, 2);
                            else
                                newValue = "100";
                        } else {
                            newValue = "" + num;
                        }
                    }
                }
                String text = newValue;
                this.prev = newValue;
                control.setText(text);
            }
        };
    }
}