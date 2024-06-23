package backend.entities;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonSetter;

import backend.DataUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "genres")
public class Genre {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "genres")
	private List<Movie> movies;
	
	@Column(nullable = false, unique = true)
	@NotBlank
	private String name;
	
	public Genre() {
	}
	
	public Genre(@NotBlank String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public void setGenreName(String name) {
		this.name = name;
	}

	/*@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if(obj instanceof String) {
			return DataUtils.equalsIgnoreCase(name, (String) obj);
		}
		if (getClass() != obj.getClass())
			return false;
		Genre other = (Genre) obj;
		return DataUtils.equalsIgnoreCase(name, other.name);
	}*/

	/*@Override
	public String toString() {
		return "MediaGenre [id=" + id + ", genreName=" + name + ", genreID=" + genreID + "]";
	}*/

}
