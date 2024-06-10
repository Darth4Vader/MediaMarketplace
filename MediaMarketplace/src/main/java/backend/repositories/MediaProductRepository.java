package backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.entities.MediaGenre;
import backend.entities.MediaProduct;
import backend.entities.Role;

@Repository
public interface MediaProductRepository extends JpaRepository<MediaProduct, Long> {
	
	Optional<MediaProduct> findByMediaName(@Param("media_name") String mediaName);
	
	Optional<MediaProduct> findByMediaID(@Param("media_id") String mediaID);
}

