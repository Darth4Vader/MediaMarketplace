package backend.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.entities.Cart;
import backend.entities.CartProduct;
import backend.entities.MediaProduct;
import backend.entities.MediaPurchased;
import backend.entities.Order;
import backend.entities.User;
import backend.repositories.MediaPurchasedRepository;
import backend.repositories.OrderRepository;

@Service
public class OrderService {
	
	@Autowired
	private OrderRepository orderRepository;
	
	/*@Autowired
	private MediaPurchasedRepository mediaPurchasedRepository;*/
	
	@Autowired
	private CartService cartService;
	
    public List<Order> getUserOrders(User user) {
    	return orderRepository.findByUser(user).get();
    }
	
    @Transactional
    public void placeOrder(User user) {
    	Cart cart = cartService.getUserCart(user);
    	List<CartProduct> cartProducts = cart.getCartProducts();
    	//List<MediaPurchased> orderList = new ArrayList<>();
    	double totalPrice = 0;
    	Order order = new Order();
    	for(CartProduct cartProduct : cartProducts) {
    		MediaPurchased orderItem = new MediaPurchased();
    		MediaProduct product = cartProduct.getProduct();
    		double price = product.getPrice();
    		orderItem.setMediaProduct(product);
    		orderItem.setPurchasePrice(price);
    		totalPrice += price;
    		order.addToPurchasedItems(orderItem);
    	}
    	order.setPurchasedDate(new Date());
    	order.setTotalPrice(totalPrice);
    	order.setUser(user);
    	orderRepository.save(order);
    	
    	/*
    	System.out.println("Added: " + mediaPurchasedRepository.findByOrder(order));
    	orderRepository.delete(order);
    	System.out.println("Delete: " + mediaPurchasedRepository.findByOrder(order));
    	*/
    }
	
}
