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
import org.springframework.transaction.annotation.Transactional;

import backend.dto.cart.AddProductToCartDto;
import backend.entities.Cart;
import backend.entities.CartProduct;
import backend.entities.Genre;
import backend.entities.Movie;
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
    private MovieService productService;
    
    public List<CartProduct> getCartProducts(User user) {
    	Cart cart = getUserCart(user);
    	return cart.getCartProducts();
    }
    
    @Transactional
    public void addProductToCart(AddProductToCartDto dto, User user) throws EntityNotFoundException, EntityAlreadyExistsException {
    	Movie product = productService.getMediaByID(dto.getProductId());
    	Cart cart = getUserCart(user);
    	List<CartProduct> cartProducts = cart.getCartProducts();
    	System.out.println(cartProducts);
    	for(CartProduct cartProduct : cartProducts) {
    		System.out.println(cartProduct.getProduct());
    		System.out.println(product);
    		System.out.println();
    		if(cartProduct.getProduct().equals(product))
    			throw new EntityAlreadyExistsException("The product is already in the cart");
    	}
    	//update
    	/*CartProduct cartProduct = new CartProduct(product, cart);
    	cartProductRepository.save(cartProduct);
    	cartProducts.add(cartProduct);*/
    }
    
    @Transactional
    public void removeProductFromCart(AddProductToCartDto dto, User user) throws EntityNotFoundException {
    	Movie product = productService.getMediaByID(dto.getProductId());
    	Cart cart = getUserCart(user);
    	List<CartProduct> cartProducts = cart.getCartProducts();
    	for(CartProduct cartProduct : cartProducts) {
    		if(cartProduct.getProduct().equals(product)) {
    	    	cartProducts.add(cartProduct);
    	    	cartProductRepository.delete(cartProduct);
    	    	return;
    		}
    	}
    	throw new EntityNotFoundException("The product is not in the cart");
    }
    
    public Cart getUserCart(User user) {
    	System.out.println(user.getUsername());
    	System.out.println(user.getId());
    	Optional<Cart> cartOpt = cartRepository.findByUser(user);
    	System.out.println(cartOpt);
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
