package backend.exceptions;

public class UserAlreadyExistsException extends Exception {

	public UserAlreadyExistsException() {
		super("The user already exists");
	}

}
