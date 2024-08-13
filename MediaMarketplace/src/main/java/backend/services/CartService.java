package backend.services;

import java.time.Instant;
import java.util.ArrayList;
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

import backend.dto.cart.CartDto;
import backend.dto.cart.CartProductDto;
import backend.dto.cart.CartProductReference;
import backend.dto.mediaProduct.ProductDto;
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
    
	@Autowired
	private TokenService tokenService;
    
    public CartDto getCart() {
    	User user = tokenService.getCurretUser();
    	Cart cart = getUserCart(user);
    	CartDto cartDto = new CartDto();
    	List<CartProduct> cartProducts = cart.getCartProducts();
    	List<CartProductDto> cartProductsDtos = new ArrayList<>();
		double totalPrice = 0;
    	if(cartProducts != null) 
    		for(CartProduct cartProduct : cartProducts) if(cartProduct != null) {
    			CartProductDto cartProductDto = new CartProductDto();
    			Product product = cartProduct.getProduct();
    			ProductDto productDto = ProductService.convertProductToDto(product);
    			cartProductDto.setProduct(productDto);
    			boolean isBuy = cartProduct.isBuying();
    			cartProductDto.setBuying(isBuy);
    			double price = calculateCartProductPrice(product, isBuy);
    			cartProductDto.setPrice(price);
    			totalPrice += price;
    			cartProductsDtos.add(cartProductDto);
    		}
    	cartDto.setCartProducts(cartProductsDtos);
    	cartDto.setTotalPrice(totalPrice);
    	return cartDto;
    }
    
    public List<CartProduct> getCartProducts(User user) {
    	Cart cart = getUserCart(user);
    	return cart.getCartProducts();
    }
    
    @Transactional
    public void addProductToCart(CartProductReference dto, User user) throws EntityNotFoundException, EntityAlreadyExistsException {
    	Product product = productService.getProductByID(dto.getProductId());
    	Cart cart = getUserCart(user);
    	System.out.println("I am here");
    	
    	
    	CartProduct productInCart = getProductInCart(cart, product, dto);
    	if(productInCart != null) {
    		System.out.println("\n\n chris re");
	    	if(dto.isBuying() == productInCart.isBuying())
				throw new EntityAlreadyExistsException("The product is already in the cart");
	    	removeProductFromCart(cart, product);
    	}
    	//add product to cart
    	System.out.println("I am buying: " + dto.isBuying());
    	CartProduct cartProduct = new CartProduct();
    	cartProduct.setProduct(product);
    	cartProduct.setCart(cart);
    	cartProduct.setBuying(dto.isBuying());
    	cartProductRepository.save(cartProduct);
    	List<CartProduct> cartProducts = cart.getCartProducts();
    	cartProducts.add(cartProduct);
    }
    
    @Transactional
    public void removeProductFromCart(CartProductReference dto, User user) throws EntityNotFoundException {
    	Product product = productService.getProductByID(dto.getProductId());
    	Cart cart = getUserCart(user);
    	removeProductFromCart(cart, product);
    }
    
    @Transactional
    private void removeProductFromCart(Cart cart, Product product) throws EntityNotFoundException {
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
    
    /*private void checkIfProductNotInCart(Cart cart, Product product, CartProductDto dto) throws EntityAlreadyExistsException {
    	List<CartProduct> cartProducts = cart.getCartProducts();
    	System.out.println(cartProducts);
    	System.out.println(product.getId());
    	for(CartProduct cartProduct : cartProducts) {
    		System.out.println(cartProduct.getProduct());
    		System.out.println(product);
    		System.out.println();
    		if(cartProduct.getProduct().equals(product)) {
    			if(dto.isBuying() == cartProduct.isBuying())
    				throw new EntityAlreadyExistsException("The product is already in the cart");
    		}
    	}
    }*/
    
    private CartProduct getProductInCart(Cart cart, Product product, CartProductReference dto) throws EntityAlreadyExistsException {
    	List<CartProduct> cartProducts = cart.getCartProducts();
    	System.out.println(cartProducts);
    	System.out.println(product.getId());
    	for(CartProduct cartProduct : cartProducts) {
    		System.out.println(cartProduct.getProduct());
    		System.out.println(product);
    		System.out.println();
    		if(cartProduct.getProduct().equals(product)) {
    			return cartProduct;
    			/*if(dto.isBuying() == cartProduct.isBuying())
    				throw new EntityAlreadyExistsException("The product is already in the cart");*/
    		}
    	}
    	return null;
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
    
	public static double calculateCartProductPrice(Product product, boolean isBuy) {
		if(isBuy)
			return ProductService.calculateBuyPrice(product);
		return ProductService.calculateRentPrice(product);
	}
    
}
