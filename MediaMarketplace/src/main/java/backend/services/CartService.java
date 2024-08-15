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

/**
 * Service class for managing user shopping carts.
 * This class provides methods to manage products within a user's cart,
 * including retrieving, adding, and removing products.
 * This is the business layer of the Spring application, handling all 
 * logic operations related to shopping cart functionality.
 */
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
    
    /**
     * Retrieves the current user's shopping cart as a dto.
     *
     * @return A {@link CartDto} representing the user's cart.
     * @throws EntityNotFoundException if the user does not have a cart
     */
    public CartDto getCart() throws EntityNotFoundException {
    	//first we load the current user cart
    	User user = tokenService.getCurretUser();
    	Cart cart = getCartByUser(user);
    	//and then we convert it to a cart dto
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
    
    /**
     * Adds a product to the user's shopping cart.
     * This method allows a user to add a product with a specified purchase type
     * (buying or renting) to their cart.
     *
     * @param cartProductReference A {@link CartProductReference} containing product ID and
     *            buying type.
     * @throws EntityNotFoundException if the product is not found.
     * @throws EntityAlreadyExistsException if the product is already in the 
     *                                       cart with the same purchase type.
     */
    @Transactional
    public void addProductToCart(CartProductReference cartProductReference) throws EntityNotFoundException, EntityAlreadyExistsException {
    	//first load or create the user's cart.
    	User user = tokenService.getCurretUser();
    	Product product = productService.getProductByID(cartProductReference.getProductId());
    	Cart cart;
    	try {
    		//load the cart
    		cart = getCartByUser(user);
    	}
    	catch (EntityNotFoundException e) {
    		//this happens when the user does not have a cart, so we will create one for him
    		cart = createCart(user);
		}
    	//first check if the product is already inside the cart with the same purchase type
    	CartProduct productInCart = getProductInCart(cart, product);
    	if(productInCart != null) {
	    	if(cartProductReference.isBuying() == productInCart.isBuying())
				throw new EntityAlreadyExistsException("The Product is already in the Cart ");
	    	//if wan to buy instead of rent, then it will be ignored, or the opposite, then we will remove the current product in cart, and then add it as new with the purchase type
	    	removeProductFromCart(cart, productInCart);
    	}
    	//add product with the purchasing type to cart
    	CartProduct cartProduct = new CartProduct();
    	cartProduct.setProduct(product);
    	cartProduct.setCart(cart);
    	cartProduct.setBuying(cartProductReference.isBuying());
    	addProductToCart(cart, cartProduct);
    }
    
    /**
     * Removes a product from the user's shopping cart.
     * This method allows a user to remove a specified product from their cart.
     *
     * @param cartProductReference A {@link CartProductReference} containing product ID to 
     *            be removed.
     * @throws EntityNotFoundException if the product is not found or the user does not have a cart.
     */
    @Transactional
    public void removeProductFromCart(CartProductReference cartProductReference) throws EntityNotFoundException {
    	//load the cart and the product
    	User user = tokenService.getCurretUser();
    	Cart cart = getCartByUser(user);
    	Product product = productService.getProductByID(cartProductReference.getProductId());
    	//now remove the product from the cart.
    	removeProductFromCart(cart, product);
    }
    
    /**
     * Creates a new cart for the specified user.
     * This method initializes a new cart for a user who does not yet have one.
     *
     * @param user The user for whom the cart will be created.
     * @return The created {@link Cart}.
     */
    @Transactional
    public Cart createCart(User user) {
    	Cart cart = new Cart(user);
    	return cartRepository.save(cart);
    }
    
    /**
     * Retrieves the cart associated with the specified user.
     *
     * @param user The user whose cart is to be retrieved.
     * @return The user's {@link Cart}.
     * @throws EntityNotFoundException if the user does not have a cart.
     */
    public Cart getCartByUser(User user) throws EntityNotFoundException {
    	return cartRepository.findByUser(user)
    			.orElseThrow(() -> new EntityNotFoundException("The user does not have a cart"));
    }
    
    /**
     * Retrieves the product from the user's cart.
     *
     * @param cart The user's cart.
     * @param product The product to search for.
     * @return The {@link CartProduct} if found, null otherwise.
     */
    private CartProduct getProductInCart(Cart cart, Product product) {
    	List<CartProduct> cartProducts = cart.getCartProducts();
    	return getProductInCart(cartProducts, product);
    }
    
    /**
     * Searches for a product in the list of cart products.
     *
     * @param cartProducts The list of cart products.
     * @param product The product to search for.
     * @return The {@link CartProduct} if found, null otherwise.
     */
    private CartProduct getProductInCart(List<CartProduct> cartProducts, Product product) {
    	for(CartProduct cartProduct : cartProducts) {
    		if(cartProduct.getProduct().equals(product)) {
    			return cartProduct;
    		}
    	}
    	return null;
    }
    
    /**
     * Adds a product to the specified user's cart.
     * 
     * @param cart The user's cart.
     * @param cartProduct The product to add to the cart.
     */
    @Transactional
    private void addProductToCart(Cart cart, CartProduct cartProduct) {
    	//first we save the cart product in to the database
    	cartProductRepository.save(cartProduct);
    	//then we add the CartProduct into the Cart
    	List<CartProduct> cartProducts = cart.getCartProducts();
    	cartProducts.add(cartProduct);
    }
    
    /**
     * Removes a specific product from the user's cart.
     *
     * @param cart The user's cart from which the product will be removed.
     * @param product The product to be removed from the cart.
     * @throws EntityNotFoundException if the product is not found in the cart.
     */
    @Transactional
    private void removeProductFromCart(Cart cart, Product product) throws EntityNotFoundException {
    	List<CartProduct> cartProducts = cart.getCartProducts();
    	//search if the product is in the cart, have a CartProduct
    	CartProduct productInCart = getProductInCart(cartProducts, product);
    	if(productInCart != null) {//if the product is in the cart then remove is
    		removeProductFromCart(cartProducts, productInCart);
	    	return;
		}
    	else//if we didn't remove the product, then it is not in the cart
    		throw new EntityNotFoundException("The product \"" + product.getId() +"\" is not in the cart");
    }
    
    /**
     * Removes a product from the specified user's cart.
     *
     * @param cart The user's cart.
     * @param cartProduct The product to be removed.
     */
    @Transactional
    private void removeProductFromCart(Cart cart, CartProduct cartProduct) {
    	removeProductFromCart(cart.getCartProducts(), cartProduct);
    }
    
    /**
     * Removes a product from the specified list of cart products.
     *
     * @param cartProducts The list of cart products.
     * @param cartProduct The product to be removed.
     */
    @Transactional
    private void removeProductFromCart(List<CartProduct> cartProducts, CartProduct cartProduct) {
    	//first remove the CartProduct from the cart's list
    	cartProducts.remove(cartProduct);
    	//then remove the CartProduct from the database.
    	cartProductRepository.delete(cartProduct);
    }
    
    /**
     * Deletes the specified cart from the user.
     * This method removes the entire cart associated with the user.
     *
     * @param cart The cart to be deleted.
     */
    @Transactional
    public void removeCartFromUser(Cart cart) {
		cartRepository.delete(cart);
    }
    
    /**
     * Calculates the price of a product based on the buying type.
     *
     * @param product The product for which to calculate the price.
     * @param isBuy True if the product is being purchased, false if rented.
     * @return The calculated price of the product.
     */
	public static double calculateCartProductPrice(Product product, boolean isBuy) {
		if(isBuy)
			return ProductService.calculateBuyPrice(product);
		return ProductService.calculateRentPrice(product);
	}   
}