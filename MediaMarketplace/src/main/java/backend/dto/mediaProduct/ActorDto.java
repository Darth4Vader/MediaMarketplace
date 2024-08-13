package backend.dto.mediaProduct;

public class ActorDto {
	
	private Long movieId;
	private PersonDto person;
	private String roleName;

	public ActorDto() {
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

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

}
