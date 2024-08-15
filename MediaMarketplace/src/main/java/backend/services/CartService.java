package backend.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.dto.cart.CartDto;
import backend.dto.cart.CartProductDto;
import backend.dto.cart.CartProductReference;
import backend.dto.mediaProduct.ProductDto;
import backend.entities.Cart;
import backend.entities.CartProduct;
import backend.entities.Product;
import backend.entities.User;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.CartProductRepository;
import backend.repositories.CartRepository;

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
    
    public CartDto getCart() throws EntityNotFoundException {
    	User user = tokenService.getCurretUser();
    	Cart cart = getCartByUser(user);
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
    
    @Transactional
    public void addProductToCart(CartProductReference dto) throws EntityNotFoundException, EntityAlreadyExistsException {
    	User user = tokenService.getCurretUser();
    	Product product = productService.getProductByID(dto.getProductId());
    	Cart cart;
    	try {
    		cart = getCartByUser(user);
    	}
    	catch (EntityNotFoundException e) {
    		//this happens when the user does not have a cart, so we will create one for him
    		cart = createCart(user);
		}
    	//first check if the product is already inside the cart with the same purchase type
    	CartProduct productInCart = getProductInCart(cart, product);
    	if(productInCart != null) {
	    	if(dto.isBuying() == productInCart.isBuying())
				throw new EntityAlreadyExistsException("The Product is already in the Cart ");
	    	//if wan to buy instead of rent, then it will be ignored, or the opposite, then we will remove the current product in cart, and then add it as new with the purchase type
	    	removeProductFromCart(cart, product);
    	}
    	//add product to cart
    	CartProduct cartProduct = new CartProduct();
    	cartProduct.setProduct(product);
    	cartProduct.setCart(cart);
    	cartProduct.setBuying(dto.isBuying());
    	addProductToCart(cart, cartProduct);
    }
    
    @Transactional
    public void removeProductFromCart(CartProductReference dto) throws EntityNotFoundException {
    	User user = tokenService.getCurretUser();
    	Cart cart = getCartByUser(user);
    	Product product = productService.getProductByID(dto.getProductId());
    	removeProductFromCart(cart, product);
    }
    
    @Transactional
    private void removeProductFromCart(Cart cart, Product product) throws EntityNotFoundException {
    	List<CartProduct> cartProducts = cart.getCartProducts();
    	CartProduct productInCart = getProductInCart(cartProducts, product);
    	if(productInCart != null) {
    		removeProductFromCart(cartProducts, productInCart);
	    	return;
		}
    	else//if we didn't remove the product, then it is not in the cart
    		throw new EntityNotFoundException("The product \"" + product.getId() +"\" is not in the cart");
    }
    
    @Transactional
    public void removeCartFromUser(Cart cart) {
		cartRepository.delete(cart);
    }
    
    @Transactional
    public Cart createCart(User user) {
    	Cart cart = new Cart(user);
    	return cartRepository.save(cart);
    }
    
    @Transactional
    private void addProductToCart(Cart cart, CartProduct cartProduct) {
    	cartProductRepository.save(cartProduct);
    	List<CartProduct> cartProducts = cart.getCartProducts();
    	cartProducts.add(cartProduct);
    }
    
    @Transactional
    private void removeProductFromCart(List<CartProduct> cartProducts, CartProduct cartProduct) {
    	cartProducts.remove(cartProduct);
    	cartProductRepository.delete(cartProduct);
    }
    
    public Cart getCartByUser(User user) throws EntityNotFoundException {
    	return cartRepository.findByUser(user)
    			.orElseThrow(() -> new EntityNotFoundException("The user does not have a cart"));
    }
    
    private CartProduct getProductInCart(Cart cart, Product product) {
    	List<CartProduct> cartProducts = cart.getCartProducts();
    	return getProductInCart(cartProducts, product);
    }
    
    private CartProduct getProductInCart(List<CartProduct> cartProducts, Product product) {
    	for(CartProduct cartProduct : cartProducts) {
    		if(cartProduct.getProduct().equals(product)) {
    			return cartProduct;
    		}
    	}
    	return null;
    }
    
	public static double calculateCartProductPrice(Product product, boolean isBuy) {
		if(isBuy)
			return ProductService.calculateBuyPrice(product);
		return ProductService.calculateRentPrice(product);
	}
    
}
