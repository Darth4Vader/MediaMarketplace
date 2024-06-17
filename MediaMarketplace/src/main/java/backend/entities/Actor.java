package backend.entities;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "actors")
public class Actor {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	private String name;
	
	@Column(name = "image_path")
	private String imagePath;
	
	@OneToMany(fetch = FetchType.EAGER, targetEntity = ActorRole.class)
	@JoinColumn(name = "actor_roles")
	private List<ActorRole> actorRoles;

	public Actor(Long id, @NotBlank String name, List<ActorRole> actorRoles) {
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

	public List<ActorRole> getActorRoles() {
		return actorRoles;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setActorRoles(List<ActorRole> actorRoles) {
		this.actorRoles = actorRoles;
	}
	
}
