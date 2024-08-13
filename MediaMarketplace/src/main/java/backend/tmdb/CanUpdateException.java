package backend.tmdb;

import backend.dto.mediaProduct.CreateMovieDto;
import backend.exceptions.EntityAlreadyExistsException;
import info.movito.themoviedbapi.model.MovieDb;

/**
 * This is thrown for the user to have the ability to update an exising movie if he want to
 */
public class CanUpdateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private CreateMovieDto createMovieDto;
	private EntityAlreadyExistsException exception;
	
	public CanUpdateException(CreateMovieDto createMovieDto, EntityAlreadyExistsException exception) {
		this.createMovieDto = createMovieDto;
		this.exception = exception;
	}
	
	public CreateMovieDto getCreateMovieDto() {
		return createMovieDto;
	}
	
	public EntityAlreadyExistsException getException() {
		return exception;
	}
	
	public void setCreateMovieDto(CreateMovieDto createMovieDto) {
		this.createMovieDto = createMovieDto;
	}
	
	public void setException(EntityAlreadyExistsException exception) {
		this.exception = exception;
	}
}
