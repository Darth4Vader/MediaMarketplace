package backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.entities.Actor;
import backend.entities.Genre;
import backend.entities.Movie;
import backend.entities.Role;
import backend.entities.User;
import backend.entities.WishList;

@Repository
public interface WishListRepository extends JpaRepository<WishList, Long> {
	
}
