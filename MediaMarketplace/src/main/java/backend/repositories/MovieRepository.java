package backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.entities.Genre;
import backend.entities.Movie;
import backend.entities.Role;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
	
	Optional<Movie> findByName(String name);
	
	Optional<Movie> findByMediaID(@Param("media_id") String mediaID);
}

