package backend.dto.input;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;

public class DirectorReference {
	
	@NotBlank
	private String personMediaID;
	
	@Nonnull
	private Long movieId;
	
	@Nonnull
	private String movieMediaId;
	
	@Nonnull
	private Long personId;

	public String getPersonMediaID() {
		return personMediaID;
	}

	public String getMovieMediaId() {
		return movieMediaId;
	}

	public Long getPersonId() {
		return personId;
	}

	public void setPersonMediaID(String personMediaID) {
		this.personMediaID = personMediaID;
	}

	public void setMovieMediaId(String movieMediaId) {
		this.movieMediaId = movieMediaId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public Long getMovieId() {
		return movieId;
	}

	public void setMovieId(Long movieId) {
		this.movieId = movieId;
	}

}
