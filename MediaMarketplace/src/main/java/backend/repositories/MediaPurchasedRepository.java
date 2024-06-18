package backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.entities.MediaPurchased;
import backend.entities.Order;
import backend.entities.User;

@Repository
public interface MediaPurchasedRepository extends JpaRepository<MediaPurchased, Long> {
	
	List<MediaPurchased> findByOrder(Order order);
	
	List<MediaPurchased> findByOrderUser(User user);
}
