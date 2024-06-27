package backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.entities.Movie;
import backend.entities.MoviePurchased;
import backend.entities.Order;
import backend.entities.User;

@Repository
public interface MoviePurchasedRepository extends JpaRepository<MoviePurchased, Long> {
	
	List<MoviePurchased> findByOrder(Order order);
	
	List<MoviePurchased> findByOrderUser(User user);
	
	Optional<List<MoviePurchased>> findAllByOrderUserAndMovie(User user, Movie movie);
}
