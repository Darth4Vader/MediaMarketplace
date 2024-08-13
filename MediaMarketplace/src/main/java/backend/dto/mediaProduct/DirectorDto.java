package backend.dto.mediaProduct;

public class DirectorDto {

	private Long movieId;
	private PersonDto person;
	
	public DirectorDto() {
		// TODO Auto-generated constructor stub
	}

	public Long getMovieId() {
		return movieId;
	}

	public PersonDto getPerson() {
		return person;
	}

	public void setMovieId(Long movieId) {
		this.movieId = movieId;
	}

	public void setPerson(PersonDto person) {
		this.person = person;
	}

}
