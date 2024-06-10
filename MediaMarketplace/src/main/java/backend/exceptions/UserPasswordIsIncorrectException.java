package backend.exceptions;

public class UserPasswordIsIncorrectException extends Exception {

	public UserPasswordIsIncorrectException() {
		super("The password is incorrect for the given username");
	}

	public UserPasswordIsIncorrectException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public UserPasswordIsIncorrectException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public UserPasswordIsIncorrectException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public UserPasswordIsIncorrectException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
