package backend.dto.mediaProduct;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

public class MovieDto {

	private String tmdbID;
	
	private String mediaName;
	
	private String imagePath;
	
	private List<String> genres;
	
	private String synopsis;

	public MovieDto() {
		
	}
	
	public MovieDto(String tmdbID, String mediaName, String imagePath, double price, List<String> genres,
			String synopsis) {
		super();
		this.tmdbID = tmdbID;
		this.mediaName = mediaName;
		this.imagePath = imagePath;
		this.genres = genres;
		this.synopsis = synopsis;
	}

	public String getMediaID() {
		return tmdbID;
	}

	public String getMediaName() {
		return mediaName;
	}

	public String getImagePath() {
		return imagePath;
	}
	
	public List<String> getGenres() {
		return genres;
	}

	public void setMediaID(String movieID) {
		this.tmdbID = movieID;
	}

	public void setMediaName(String mediaName) {
		this.mediaName = mediaName;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getSynopsis() {
		return synopsis;
	}

	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}

	public void setGenres(List<String> genres) {
		this.genres = genres;
	}

}
