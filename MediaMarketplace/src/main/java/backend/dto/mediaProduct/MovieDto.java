package backend.dto.mediaProduct;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

public class MovieDto {

	private String tmdbID;
	
	private String mediaName;
	
	private String posterPath;
	
	private String backdropPath;
	
	private Integer runtime;
	
	private List<String> genres;
	
	private String synopsis;
	
	private double year;
	
	private LocalDate releaseDate;
	
	/*private List<DirectorDto> directrs;
	
	private List<ActorDto> actors;*/

	public MovieDto() {
		
	}
	
	public MovieDto(String tmdbID, String mediaName, String imagePath, double price, List<String> genres,
			String synopsis) {
		super();
		this.tmdbID = tmdbID;
		this.mediaName = mediaName;
		this.posterPath = imagePath;
		this.genres = genres;
		this.synopsis = synopsis;
	}

	public String getMediaID() {
		return tmdbID;
	}

	public String getMediaName() {
		return mediaName;
	}

	public String getPosterPath() {
		return posterPath;
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

	public void setPosterPath(String posterPath) {
		this.posterPath = posterPath;
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

	public double getYear() {
		return year;
	}

	public LocalDate getReleaseDate() {
		return releaseDate;
	}

	public void setYear(double year) {
		this.year = year;
	}

	public void setReleaseDate(LocalDate releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getBackdropPath() {
		return backdropPath;
	}

	public Integer getRuntime() {
		return runtime;
	}

	public void setBackdropPath(String backdropPath) {
		this.backdropPath = backdropPath;
	}

	public void setRuntime(Integer runtime) {
		this.runtime = runtime;
	}

	/*public List<DirectorDto> getDirectrs() {
		return directrs;
	}

	public List<ActorDto> getActors() {
		return actors;
	}

	public void setDirectrs(List<DirectorDto> directrs) {
		this.directrs = directrs;
	}

	public void setActors(List<ActorDto> actors) {
		this.actors = actors;
	}*/

}
