package backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import backend.entities.Cart;
import backend.entities.Order;
import backend.entities.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	
	Optional<List<Order>> findByUser(User user);
	
}
