package backend.tmdb;

public class NameAndException {

	private String name;
	private Exception exception;
	
	public NameAndException(String name, Exception exception) {
		super();
		this.name = name;
		this.exception = exception;
	}

	public String getName() {
		return name;
	}

	public Exception getException() {
		return exception;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

}
