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

import backend.dto.cart.CartProductDto;
import backend.entities.Cart;
import backend.entities.CartProduct;
import backend.entities.Genre;
import backend.entities.Movie;
import backend.entities.Product;
import backend.entities.User;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.CartProductRepository;
import backend.repositories.CartRepository;
import backend.repositories.GenreRepository;
import backend.repositories.ProductRepository;
import backend.repositories.UserRepository;

@Service
public class CartService {
	
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private CartProductRepository cartProductRepository;
    
    @Autowired
    private ProductService productService;
    
    public List<CartProduct> getCartProducts(User user) {
    	Cart cart = getUserCart(user);
    	return cart.getCartProducts();
    }
    
    @Transactional
    public void addProductToCart(CartProductDto dto, User user) throws EntityNotFoundException, EntityAlreadyExistsException {
    	Product product = productService.getProductByID(dto.getProductId());
    	Cart cart = getUserCart(user);
    	checkIfProductNotInCart(cart, product);
    	//add product to cart
    	CartProduct cartProduct = new CartProduct(product, cart, dto.isBuying());
    	cartProductRepository.save(cartProduct);
    	List<CartProduct> cartProducts = cart.getCartProducts();
    	cartProducts.add(cartProduct);
    }
    
    @Transactional
    public void removeProductFromCart(CartProductDto dto, User user) throws EntityNotFoundException {
    	Product product = productService.getProductByID(dto.getProductId());
    	Cart cart = getUserCart(user);
    	List<CartProduct> cartProducts = cart.getCartProducts();
    	for(CartProduct cartProduct : cartProducts) {
    		if(cartProduct.getProduct().equals(product)) {
    	    	//System.out.println("Let's remove mr.: " + cartProduct);
    	    	cartProducts.remove(cartProduct);
    	    	cartProductRepository.delete(cartProduct);
    	    	/*try {
					checkIfProductNotInCart(getUserCart(user), product);
					System.out.println("Good Green");
				} catch (EntityAlreadyExistsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
    	    	return;
    		}
    	} //if we didn't remove the product, then it is not in the cart
    	throw new EntityNotFoundException("The product is not in the cart");
    }
    
    @Transactional
    public void removeCartFromUser(Cart cart) {
    	cartRepository.delete(cart);
    }
    
    private void checkIfProductNotInCart(Cart cart, Product product) throws EntityAlreadyExistsException {
    	List<CartProduct> cartProducts = cart.getCartProducts();
    	System.out.println(cartProducts);
    	System.out.println(product.getId());
    	for(CartProduct cartProduct : cartProducts) {
    		System.out.println(cartProduct.getProduct());
    		System.out.println(product);
    		System.out.println();
    		if(cartProduct.getProduct().equals(product))
    			throw new EntityAlreadyExistsException("The product is already in the cart");
    	}
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
    
}
