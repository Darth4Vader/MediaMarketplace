package backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "actors")
public class Actor {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "role_name", nullable = false)
	@NotBlank
	private String roleName;
	
    @ManyToOne
    @JoinColumn(name = "movie_id", referencedColumnName = "id", nullable = false)
	private Movie movie;
	
    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id", nullable = false)
	private Person person;

	public Long getId() {
		return id;
	}

	public String getRoleName() {
		return roleName;
	}

	public Movie getMedia() {
		return movie;
	}

	public Person getPerson() {
		return person;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public void setMedia(Movie media) {
		this.movie = media;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
}
