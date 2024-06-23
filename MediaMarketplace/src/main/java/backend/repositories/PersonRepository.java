package backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.entities.Actor;
import backend.entities.Genre;
import backend.entities.Movie;
import backend.entities.Person;
import backend.entities.Role;
import backend.entities.User;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
	
	Optional<Person> findByPersonImdbId(@Param("person_imdb_id") String personImdbID);
}
