package backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import backend.entities.Cart;
import backend.entities.User;
import java.util.List;


@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
	
	Optional<Cart> findByUser(@Param("user_name") User user);
	
}
