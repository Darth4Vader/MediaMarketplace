package backend.entities;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "people")
public class Person {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "person_imdb_id", unique = true)
	private String personImdbId;
	
	@Column(nullable = false)
	@NotBlank
	private String name;
	
	@Column(name = "image_path")
	private String imagePath;
	
	@Column(name = "birth_date")
	private Date birthDate;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "person", cascade = CascadeType.ALL)
	private List<Actor> actorRoles;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "person", cascade = CascadeType.ALL)
	private List<Director> directedMedia;
	
	public Person() {}

	public Person(Long id, @NotBlank String name, List<Actor> actorRoles) {
		super();
		this.id = id;
		this.name = name;
		this.actorRoles = actorRoles;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<Actor> getActorRoles() {
		return actorRoles;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setActorRoles(List<Actor> actorRoles) {
		this.actorRoles = actorRoles;
	}

	public String getPersonImdbId() {
		return personImdbId;
	}

	public String getImagePath() {
		return imagePath;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public List<Director> getDirectedMedia() {
		return directedMedia;
	}

	public void setPersonImdbId(String personImdbId) {
		this.personImdbId = personImdbId;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public void setDirectedMedia(List<Director> directedMedia) {
		this.directedMedia = directedMedia;
	}
}
