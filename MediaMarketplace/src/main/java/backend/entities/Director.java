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
	
	@Column(name = "role_name", nullable = false)
	@NotBlank
	private String roleName;
	
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "media_id", referencedColumnName = "id", nullable = false)
	private MediaProduct media;
	
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "director_id", nullable = false)
	private Person director;

	public Long getId() {
		return id;
	}

	public String getRoleName() {
		return roleName;
	}

	public MediaProduct getMedia() {
		return media;
	}

	public Person getDirector() {
		return director;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public void setMedia(MediaProduct media) {
		this.media = media;
	}

	public void setDirector(Person director) {
		this.director = director;
	}

}
