package backend.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "movies")
public class Movie {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "media_id", nullable = false, unique = true)
	private String mediaID;
	
	@NotBlank
	private String name;
	
	private Integer runtime;
	
	@Column(name = "poster_path")
	private String posterPath;
	
	@Column(name = "backdrop_path")
	private String backdropPath;
	
	@Column(length = 1000)
	private String synopsis;
	
	private Integer year;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	@Column(name = "release_date")
	private LocalDate releaseDate;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "movie", cascade = CascadeType.ALL)
	private List<Director> directors;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "movie", cascade = CascadeType.ALL)
	private List<Actor> actorsRoles;
	
	//@OneToMany(fetch = FetchType.LAZY, targetEntity = MediaGenre.class)
	//@JoinColumn(name = "genres", insertable = false, updatable = false)
	//@OneToMany(fetch = FetchType.LAZY, mappedBy = "media", cascade = CascadeType.ALL)
	@ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "movie_genres",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
	private List<Genre> genres;

	public Movie() {
		this.directors = new ArrayList<>();
		this.actorsRoles = new ArrayList<>();
	}

	public Long getId() {
		return id;
	}

	public String getMediaID() {
		return mediaID;
	}

	public String getName() {
		return name;
	}

	public String getPosterPath() {
		return posterPath;
	}

	public List<Genre> getGenres() {
		return genres;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setMediaID(String mediaID) {
		this.mediaID = mediaID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPosterPath(String posterPath) {
		this.posterPath = posterPath;
	}

	public void setGenres(List<Genre> genres) {
		this.genres = genres;
	}

	public String getSynopsis() {
		return synopsis;
	}

	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}

	public Integer getYear() {
		return year;
	}
	
	public boolean hasYear() {
		return year != null;
	}

	public List<Actor> getActorsRoles() {
		return actorsRoles;
	}

	public void setYear(int year) {
		this.year = year;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Movie other = (Movie) obj;
		return Objects.equals(id, other.id);
	}

	public List<Director> getDirectors() {
		return directors;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public void setDirectors(List<Director> directors) {
		this.directors = directors;
	}

	public Integer getRuntime() {
		return runtime;
	}

	public String getBackdropPath() {
		return backdropPath;
	}

	public LocalDate getReleaseDate() {
		return releaseDate;
	}

	public void setRuntime(Integer runtime) {
		this.runtime = runtime;
	}

	public void setBackdropPath(String backdropPath) {
		this.backdropPath = backdropPath;
	}

	public void setReleaseDate(LocalDate releaseDate) {
		this.releaseDate = releaseDate;
	}
	
	

}
