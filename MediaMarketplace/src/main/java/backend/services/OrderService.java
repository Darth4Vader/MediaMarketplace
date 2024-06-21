package backend.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.entities.Cart;
import backend.entities.CartProduct;
import backend.entities.Movie;
import backend.entities.MoviePurchased;
import backend.entities.Order;
import backend.entities.Product;
import backend.entities.User;
import backend.exceptions.PurchaseOrderException;
import backend.repositories.MoviePurchasedRepository;
import backend.repositories.OrderRepository;

@Service
public class OrderService {
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private CartService cartService;
	
    public List<Order> getUserOrders(User user) {
    	return orderRepository.findByUser(user).orElseThrow(null);
    }
	
    @Transactional
    public void placeOrder(User user) throws PurchaseOrderException {
    	Cart cart = cartService.getUserCart(user);
    	List<CartProduct> cartProducts = cart.getCartProducts();
    	double totalPrice = 0;
    	Order order = new Order();
    	if(cartProducts.isEmpty())
    		throw new PurchaseOrderException("The cart is empty");
    	for(CartProduct cartProduct : cartProducts) {
    		Product product = cartProduct.getProduct();
    		boolean isBuy = cartProduct.isBuying();
    		double price = product.calculatePrice(isBuy);
    		Movie movie = product.getMovie();
    		MoviePurchased orderItem = new MoviePurchased();
    		orderItem.setMovie(movie);
    		orderItem.setPurchasePrice(price);
    		totalPrice += price;
    		order.addToPurchasedItems(orderItem);
    	}
    	order.setPurchasedDate(new Date());
    	order.setTotalPrice(totalPrice);
    	order.setUser(user);
    	cartService.removeCartFromUser(cart);
    	orderRepository.save(order);
    	/*
    	System.out.println("Added: " + mediaPurchasedRepository.findByOrder(order));
    	orderRepository.delete(order);
    	System.out.println("Delete: " + mediaPurchasedRepository.findByOrder(order));
    	*/
    }
	
}
