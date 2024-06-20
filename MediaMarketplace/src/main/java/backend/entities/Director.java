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
    @JsonIgnore
    @JoinColumn(name = "movie_id", nullable = false)
	private Movie movie;
	
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "person_id", nullable = false)
	private Person director;

	public Long getId() {
		return id;
	}

	public Movie getMedia() {
		return movie;
	}

	public Person getDirector() {
		return director;
	}

	public void setMedia(Movie media) {
		this.movie = media;
	}

	public void setDirector(Person director) {
		this.director = director;
	}

}
