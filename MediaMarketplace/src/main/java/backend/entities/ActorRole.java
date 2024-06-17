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
@Table(name = "actors_roles")
public class ActorRole {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "role_name", nullable = false)
	@NotBlank
	private String roleName;
	
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "actor_id", referencedColumnName = "id", nullable = false)
	private Actor actor;
    
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "media_id", referencedColumnName = "id", nullable = false)
	private MediaProduct media;

	public Long getId() {
		return id;
	}

	public String getRoleName() {
		return roleName;
	}

	public Actor getActor() {
		return actor;
	}

	public MediaProduct getMedia() {
		return media;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public void setActor(Actor actor) {
		this.actor = actor;
	}

	public void setMedia(MediaProduct media) {
		this.media = media;
	}
}
