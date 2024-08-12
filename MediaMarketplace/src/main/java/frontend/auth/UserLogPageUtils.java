package frontend.auth;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class UserLogPageUtils {
	
	public static ChangeListener<String> textFielsListener(Node textField, Label errorLabel) {
		return new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(oldValue.trim().isEmpty() && !newValue.trim().isEmpty()) {
					textField.setStyle("");
					if(!errorLabel.getText().isEmpty())
						errorLabel.setText("");
				}
			}
		};
	}
}
