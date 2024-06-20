package backend.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "media_products")
public class MediaProduct {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "media_id", nullable = false, unique = true)
	private String mediaID;
	
	@Column(name = "media_name", nullable = false)
	@NotBlank
	private String mediaName;
	
	@Column(name = "image_path")
	private String imagePath;
	
	@Column(nullable = false)
	@NotBlank
	private double price;
	
	@Column(length = 1000)
	private String synopsis;
	
	private String year;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "media", cascade = CascadeType.ALL)
	private List<Director> directors;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "media", cascade = CascadeType.ALL)
	//@JoinColumn(name = "actors_roles_id")
	private List<ActorRole> actorsRoles;
	
	//@ManyToOne(targetEntity=MediaGenre.class, fetch = FetchType.LAZY, optional = false)
	//@JoinColumn(name = "genre_id", nullable = false)
	@OneToMany(fetch = FetchType.LAZY, targetEntity = MediaGenre.class)
	@JoinColumn(name = "genres", insertable = false, updatable = false)
	private List<MediaGenre> genres;

	public MediaProduct() {
		this.actorsRoles = new ArrayList<>();
	}

	public Long getId() {
		return id;
	}

	public String getMediaID() {
		return mediaID;
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

	public List<MediaGenre> getGenres() {
		return genres;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setMediaID(String mediaID) {
		this.mediaID = mediaID;
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

	public void setGenres(List<MediaGenre> genres) {
		this.genres = genres;
	}

	public String getSynopsis() {
		return synopsis;
	}

	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}

	public String getYear() {
		return year;
	}

	public List<ActorRole> getActorsRoles() {
		return actorsRoles;
	}

	public void setYear(String year) {
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
		MediaProduct other = (MediaProduct) obj;
		return Objects.equals(id, other.id);
	}
	
	

}
