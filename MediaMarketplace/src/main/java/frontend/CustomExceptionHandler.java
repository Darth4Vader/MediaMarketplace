package frontend;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

import org.hibernate.exception.JDBCConnectionException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authorization.AuthorizationDeniedException;

import backend.exceptions.UserNotLoggedInException;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

public class CustomExceptionHandler implements UncaughtExceptionHandler {

	private App app;
	private UncaughtExceptionHandler prevHandler;
	
	public CustomExceptionHandler(UncaughtExceptionHandler prevHandler, App app) {
		this.prevHandler = prevHandler;
		this.app = app;
	}
	
	private boolean isCausedBy(Throwable caught, Class<? extends Throwable> isOfOrCausedBy) {
		if (caught == null) return false;
		else if (isOfOrCausedBy.isAssignableFrom(caught.getClass())) return true;
	    else return isCausedBy(caught.getCause(), isOfOrCausedBy);
	}
	
	@Override
	public void uncaughtException(Thread thread, Throwable throwable) {
		//System.out.println("culybaly");
		if(isCausedBy(throwable, UserNotLoggedInException.class)) {
			userNotLogException();
		}
		else if(isCausedBy(throwable, AuthorizationDeniedException.class)) {
			AppUtils.alertOfError("Accsses Deny", "cannot do this operation");
		}
		else if(isCausedBy(throwable, AuthenticationCredentialsNotFoundException.class)) {
			userNotLogException();
		}
		else if(isCausedBy(throwable, JDBCConnectionException.class)) {
			AppUtils.alertOfError("Open Server Error", "Unable to connect to server");
		}
		else {
			//System.out.println("kkk ");
			//throwable.printStackTrace();
			//prevHandler.uncaughtException(thread, throwable);
            String name = "";
            if(thread != null)
            	name = thread.getName();
			System.err.print("Exception in thread \""
                    + name + "\" ");
            throwable.printStackTrace(System.err);
		}
	}
	
	private void userNotLogException() {
		//System.out.println("user is not logged to the system");
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("The user is not logged in");
        alert.setHeaderText("sign in or continue to browse as unlogged");
        
        Button signInBtn = createSignButton(alert, true);
        Button registerBtn = createSignButton(alert, false);
        HBox box = new HBox();
        box.setSpacing(10);
		box.setBorder(new Border(new BorderStroke(Color.PINK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
	            new BorderWidths(1))));
        //box.prefWidthProperty().bind(alert.widthProperty());
        //box.setMaxWidth(Double.MAX_VALUE);
        box.getChildren().addAll(signInBtn, registerBtn);
        alert.getDialogPane().setContent(box);
        alert.show();
	}
	
	private Button createSignButton(Alert alert, boolean logIn) {
		String text = logIn ? "Sign in" : "Register";
		Button signBtn = new Button(text);
        signBtn.setOnAction(e -> {
    		alert.close();
			this.app.activateLogPage(false);
        });
        signBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(signBtn, Priority.ALWAYS);
        signBtn.setStyle("-fx-font-weight: bold;");
        return signBtn;
	}
	
}