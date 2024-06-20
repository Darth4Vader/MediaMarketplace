package backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.entities.Genre;
import backend.entities.Movie;
import backend.entities.Role;
import backend.entities.User;

@Repository
public interface MediaGenreRepository extends JpaRepository<Genre, Long> {
	
	//Optional<MediaGenre> findByGenreName(@Param("genre_name") String genreName);
	
	Optional<Genre> findByName(String name);
	
	//Optional<MediaGenre> findByGenreID(@Param("genre_id") String genreID);
}
