package backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import backend.entities.Cart;
import backend.entities.CartProduct;
import backend.entities.User;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, Long> {
	
}
