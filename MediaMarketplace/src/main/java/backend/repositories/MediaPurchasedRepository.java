package backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.entities.MediaPurchased;
import backend.entities.Order;

@Repository
public interface MediaPurchasedRepository extends JpaRepository<MediaPurchased, Long> {
	
}
