package backend.dto.mediaProduct;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

public class MediaProductDto {

	private String tmdbID;
	
	private String mediaName;
	
	private String imagePath;
	
	private double price;
	
	private List<String> genresIDList;
	
	private String synopsis;

	public MediaProductDto() {
		
	}
	
	public MediaProductDto(String tmdbID, String mediaName, String imagePath, double price, List<String> genresIDList,
			String synopsis) {
		super();
		this.tmdbID = tmdbID;
		this.mediaName = mediaName;
		this.imagePath = imagePath;
		this.price = price;
		this.genresIDList = genresIDList;
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

	public double getPrice() {
		return price;
	}
	
	public List<String> getGenresIDList() {
		return genresIDList;
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

	public void setPrice(double price) {
		this.price = price;
	}

	public String getSynopsis() {
		return synopsis;
	}

	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}

	public void setGenres(List<String> genres) {
		this.genresIDList = genres;
	}

}
