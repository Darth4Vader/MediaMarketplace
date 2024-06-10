package backend.exceptions;

public class UserDoesNotExistsException extends Exception {

	public UserDoesNotExistsException() {
		super("The User does not exists for the given username");
	}

	public UserDoesNotExistsException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public UserDoesNotExistsException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public UserDoesNotExistsException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public UserDoesNotExistsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
