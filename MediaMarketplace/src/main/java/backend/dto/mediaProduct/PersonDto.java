package backend.dto.mediaProduct;

import java.util.Date;
import java.util.List;

import backend.entities.Actor;
import backend.entities.Director;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;

public class PersonDto {

	private Long id;
	
	private String personMediaID;
	
	@NotBlank
	private String name;
	
	private String imagePath;
	
	private Date birthDate;

	public Long getId() {
		return id;
	}

	public String getPersonMediaID() {
		return personMediaID;
	}

	public String getName() {
		return name;
	}

	public String getImagePath() {
		return imagePath;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setPersonMediaID(String personMediaID) {
		this.personMediaID = personMediaID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}
	
}
