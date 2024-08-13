package backend.dto.input;

import org.springframework.lang.NonNull;

import backend.entities.Movie;
import backend.entities.Person;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;

public class RefActorDto {
	
	@NotBlank
	private String personMediaID;
	
	@NotBlank
	private String roleName;
	
	@Nonnull
	private String movieMediaId;
	
	@Nonnull
	private Long personId;

	public String getPersonMediaID() {
		return personMediaID;
	}

	public String getRoleName() {
		return roleName;
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

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public void setMovieMediaId(String movieMediaId) {
		this.movieMediaId = movieMediaId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}
}