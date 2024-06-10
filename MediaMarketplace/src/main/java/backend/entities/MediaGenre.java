package backend.entities;

import com.fasterxml.jackson.annotation.JsonSetter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "media_genres")
public class MediaGenre {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "genre_name", nullable = false)
	@NotBlank
	private String genreName;
	
	@Column(name = "genre_id", nullable = false, unique = true)
	@NotBlank
	private String genreID;
	
	public MediaGenre() {
	}
	
	public MediaGenre(@NotBlank String genreName) {
		setGenreName(genreName);
	}

	public Long getId() {
		return id;
	}

	public String getGenreName() {
		return genreName;
	}

	public String getGenreID() {
		return genreID;
	}
	
	@JsonSetter
	public void setGenreName(String genreName) {
		this.genreName = genreName;
		this.genreID = convertGenreNameToID(genreName);
	}
	
	public static String convertGenreNameToID(String genreName) {
		return genreName.toLowerCase().replaceAll(" ", "_");
	}

	@Override
	public String toString() {
		return "MediaGenre [id=" + id + ", genreName=" + genreName + ", genreID=" + genreID + "]";
	}

}
