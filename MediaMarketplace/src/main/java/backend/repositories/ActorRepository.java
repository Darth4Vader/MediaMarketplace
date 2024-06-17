package backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.entities.Actor;
import backend.entities.MediaGenre;
import backend.entities.MediaProduct;
import backend.entities.Role;
import backend.entities.User;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Long> {
	
}
