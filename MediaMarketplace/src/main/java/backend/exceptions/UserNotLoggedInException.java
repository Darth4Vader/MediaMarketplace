package backend.exceptions;

public class UserNotLoggedInException extends RuntimeException {

	public UserNotLoggedInException() {
		// TODO Auto-generated constructor stub
	}

	public UserNotLoggedInException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public UserNotLoggedInException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public UserNotLoggedInException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public UserNotLoggedInException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
