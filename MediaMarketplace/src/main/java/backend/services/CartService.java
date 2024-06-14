package backend.services;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import backend.dto.cart.AddProductToCartDto;
import backend.entities.Cart;
import backend.entities.CartProduct;
import backend.entities.MediaGenre;
import backend.entities.MediaProduct;
import backend.entities.User;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.CartProductRepository;
import backend.repositories.CartRepository;
import backend.repositories.MediaGenreRepository;
import backend.repositories.UserRepository;

@Service
public class CartService {
	
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private CartProductRepository cartProductRepository;
    
    @Autowired
    private MediaProductService productService;
    
    public void addProductToCart(AddProductToCartDto dto, User user) throws EntityNotFoundException {
    	MediaProduct product = productService.getMediaByID(dto.getProductId());
    	Cart cart = getUserCart(user);
    	CartProduct cartProduct = new CartProduct(product, cart);
    	cartProductRepository.save(cartProduct);
    }
    
    public Cart getUserCart(User user) {
    	Optional<Cart> cartOpt = cartRepository.findByUser(user);
    	if (cartOpt.isPresent())
    		return cartOpt.get();
    	Cart cart = new Cart(user);
    	cartRepository.save(cart);
    	return cart;
    }
    
    /*public List<MediaGenre> getAllGenres() {
    	return mediaGenreRepository.findAll();
    }
	
    public void createGenre(MediaGenre genre) throws EntityAlreadyExistsException {
    	String genreID = genre.getGenreID();
    	try {	
    		getGenreByID(genreID);
    		throw new EntityAlreadyExistsException("The Genre with id: ("+genreID+") does already exists");
    	}
    	catch (EntityNotFoundException e) {}
    	mediaGenreRepository.save(genre);
    }
    
    public MediaGenre getGenreByID(String genreID) throws EntityNotFoundException {
    	return mediaGenreRepository.findByGenreID(genreID).
    			orElseThrow(() -> new EntityNotFoundException("The Genre with id: ("+genreID+") does not exists"));
    }*/
    
}
