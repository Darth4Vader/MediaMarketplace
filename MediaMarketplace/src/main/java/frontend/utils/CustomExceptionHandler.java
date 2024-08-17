package frontend.utils;

import java.lang.Thread.UncaughtExceptionHandler;

import org.hibernate.exception.JDBCConnectionException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authorization.AuthorizationDeniedException;

import backend.exceptions.EntityAccessException;
import backend.exceptions.UserNotLoggedInException;
import frontend.App;
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

/**
 * A custom exception handler that manages uncaught exceptions across threads.
 * It provides specific handling for common exceptions and displays appropriate
 * error messages to the user.
 */
public class CustomExceptionHandler implements UncaughtExceptionHandler {

	/**
	 * The {@code App} instance used for handling navigation or user actions
	 * when an uncaught exception occurs. This instance allows the exception
	 * handler to direct users to login or registration pages if necessary.
	 */
	private App app;
	
    /**
     * Constructs a {@code CustomExceptionHandler} with the specified application instance.
     *
     * @param app the application instance used for navigation or other actions
     */
	public CustomExceptionHandler(App app) {
		this.app = app;
	}
	
    /**
     * Checks if the given throwable or its cause matches the specified exception class.
     *
     * @param caught the throwable to check
     * @param isOfOrCausedBy the exception class to compare against
     * @return {@code true} if the throwable is of the specified class or caused by it; {@code false} otherwise
     */
	private boolean isCausedBy(Throwable caught, Class<? extends Throwable> isOfOrCausedBy) {
		if (caught == null) return false;
		else if (isOfOrCausedBy.isAssignableFrom(caught.getClass())) return true;
	    else return isCausedBy(caught.getCause(), isOfOrCausedBy);
	}
	
    /**
     * Handles uncaught exceptions by categorizing them and displaying appropriate alerts or logging errors.
     *
     * @param thread the thread where the exception occurred
     * @param throwable the throwable to handle
     */
	@Override
	public void uncaughtException(Thread thread, Throwable throwable) {
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
		else if(isCausedBy(throwable, EntityAccessException.class)) {
			//here we handle all the access exception that hibernate returns, but we changed it in the controller level to be a custom runtime exception.
			AppUtils.alertOfError("Accessing data exception", throwable.getMessage());
			throwable.printStackTrace();
		}
		else {
            String name = "";
            if(thread != null)
            	name = thread.getName();
			System.err.print("Exception in thread \""
                    + name + "\" ");
            throwable.printStackTrace(System.err);
		}
	}
	
    /**
     * Displays an alert indicating that the user is not logged in, with options to sign in or register.
     */
	private void userNotLogException() {
		Alert alert = AppUtils.createAlertOfError("The user is not logged in", "sign in or continue to browse as unlogged");
        Button signInBtn = createSignButton(alert, true);
        Button registerBtn = createSignButton(alert, false);
        HBox box = new HBox();
        box.setSpacing(10);
		box.setBorder(new Border(new BorderStroke(Color.PINK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
	            new BorderWidths(1))));
        box.getChildren().addAll(signInBtn, registerBtn);
        alert.getDialogPane().setContent(box);
        alert.show();
	}
	
    /**
     * Creates a sign-in or registration button for the alert dialog.
     *
     * @param alert the alert to close when the button is clicked
     * @param logIn {@code true} to create a sign-in button, {@code false} to create a register button
     * @return the created button
     */
	private Button createSignButton(Alert alert, boolean logIn) {
		String text = logIn ? "Sign in" : "Register";
		Button signBtn = new Button(text);
        signBtn.setOnAction(e -> {
    		alert.close();
			this.app.activateLogPage(logIn);
        });
        signBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(signBtn, Priority.ALWAYS);
        signBtn.setStyle("-fx-font-weight: bold;");
        return signBtn;
	}
}