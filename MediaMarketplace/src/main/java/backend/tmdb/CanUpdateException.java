package backend.tmdb;

import backend.dto.mediaProduct.MovieDto;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import info.movito.themoviedbapi.model.MovieDb;

/**
 * This is thrown for the user to have the ability to update an exising movie if he want to
 */
public class CanUpdateException extends Exception {

	private MovieDb movieDb;
	private EntityAlreadyExistsException exception;
	
	public CanUpdateException(MovieDb movieDb, EntityAlreadyExistsException exception) {
		super();
		this.movieDb = movieDb;
		this.exception = exception;
	}
	
	public MovieDb getMovieDb() {
		return movieDb;
	}
	public EntityAlreadyExistsException getException() {
		return exception;
	}
	public void setMovieDb(MovieDb movieDb) {
		this.movieDb = movieDb;
	}
	public void setException(EntityAlreadyExistsException exception) {
		this.exception = exception;
	}
}
