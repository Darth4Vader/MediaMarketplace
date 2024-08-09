package backend.tmdb;

import java.util.ArrayList;
import java.util.List;

public class CreateMovieException extends Exception {

	private List<NameAndException> list;
	
	public CreateMovieException(List<NameAndException> list, String message) {
		super(message);
		this.list = list;
	}

	public List<NameAndException> getList() {
		return list;
	}

	public void setList(List<NameAndException> list) {
		this.list = list;
	}
}
