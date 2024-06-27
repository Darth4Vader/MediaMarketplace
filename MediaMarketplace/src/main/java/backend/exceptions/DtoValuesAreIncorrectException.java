package backend.exceptions;

import java.util.HashMap;
import java.util.Map;

public class DtoValuesAreIncorrectException extends Exception {
	
	private Map<String, String> map;

	public DtoValuesAreIncorrectException(Map<String, String> map) {
		this.map = map;
	}

	public Map<String, String> getMap() {
		return map;
	}
}
