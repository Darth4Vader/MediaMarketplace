package backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "directors")
public class Director {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
	private Movie movie;
	
    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
	private Person person;

	public Long getId() {
		return id;
	}

	public Movie getMedia() {
		return movie;
	}

	public Person getPerson() {
		return person;
	}

	public void setMedia(Movie media) {
		this.movie = media;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

}
