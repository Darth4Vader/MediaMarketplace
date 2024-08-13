package backend.dto.mediaProduct;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

public class MovieDto {
	
	private Long id;
	
	private String name;
	
	private String posterPath;
	
	private String backdropPath;
	
	private Integer runtime;
	
	private List<String> genres;
	
	private String synopsis;
	
	private Integer year;
	
	private LocalDate releaseDate;

	public String getName() {
		return name;
	}

	public String getPosterPath() {
		return posterPath;
	}
	
	public List<String> getGenres() {
		return genres;
	}

	public void setName(String name) {
		this.name = name;
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

	public Integer getYear() {
		return year;
	}

	public LocalDate getReleaseDate() {
		return releaseDate;
	}

	public void setYear(Integer year) {
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
