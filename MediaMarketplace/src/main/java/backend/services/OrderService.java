package backend.services;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.dto.mediaProduct.MoviePurchasedDto;
import backend.dto.mediaProduct.OrderDto;
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
	
	@Autowired
	private TokenService tokenService;
	
    public List<OrderDto> getUserOrders() {
    	User user = tokenService.getCurretUser();
    	List<Order> orders = getOrdersMadeBuyUser(user);
    	List<OrderDto> orderDtos = new ArrayList<>();
    	if(orders != null)
    		for(Order order : orders) if(order != null) {
    			OrderDto orderDto = convertOrderToDto(order);
    			orderDtos.add(orderDto);
    		}
    	return orderDtos;
    }
    
    @Transactional
    public Order placeOrder(User user) throws PurchaseOrderException {
    	Cart cart = cartService.getUserCart(user);
    	List<CartProduct> cartProducts = cart.getCartProducts();
    	double totalPrice = 0;
    	Order order = new Order();
    	if(cartProducts.isEmpty())
    		throw new PurchaseOrderException("The cart is empty");
    	for(CartProduct cartProduct : cartProducts) {
    		Product product = cartProduct.getProduct();
    		boolean isBuy = cartProduct.isBuying();
    		System.out.println("Palapatine: " + isBuy);
    		double price = CartService.calculateCartProductPrice(product, isBuy);
    		Movie movie = product.getMovie();
    		MoviePurchased orderItem = new MoviePurchased();
    		orderItem.setMovie(movie);
    		orderItem.setPurchasePrice(price);
    		System.out.println("Are you buying friend: " + isBuy);
    		orderItem.setRented(!isBuy);
    		if(!isBuy)
    			orderItem.setRentTime(Duration.ofMinutes(3));
    			//orderItem.setRentTime(Duration.ofHours(72));
    		totalPrice += price;
    		order.addToPurchasedItems(orderItem);
    	}
    	order.setTotalPrice(totalPrice);
    	order.setUser(user);
    	cartService.removeCartFromUser(cart);
    	orderRepository.save(order);
    	return order;
    	
    	//System.out.println("Added: " + mediaPurchasedRepository.findByOrder(order));
    	//orderRepository.delete(order);
    	//System.out.println("Delete: " + mediaPurchasedRepository.findByOrder(order));
    	
    }
	
    /*
    @Transactional
    public Order placeOrder(User user) throws PurchaseOrderException {
    	Cart cart = cartService.getUserCart(user);
    	List<CartProduct> cartProducts = cart.getCartProducts();
    	double totalPrice = 0;
    	Order order = new Order();
    	if(cartProducts.isEmpty())
    		throw new PurchaseOrderException("The cart is empty");
    	for(CartProduct cartProduct : cartProducts) {
    		Product product = cartProduct.getProduct();
    		boolean isBuy = cartProduct.isBuying();
    		System.out.println("Palapatine: " + isBuy);
    		double price = product.calculatePrice(isBuy);
    		Movie movie = product.getMovie();
    		MoviePurchased orderItem = new MoviePurchased();
    		orderItem.setMovie(movie);
    		orderItem.setPurchasePrice(price);
    		System.out.println("Are you buying friend: " + isBuy);
    		orderItem.setRented(!isBuy);
    		if(!isBuy)
    			orderItem.setRentTime(Duration.ofMinutes(3));
    			//orderItem.setRentTime(Duration.ofHours(72));
    		totalPrice += price;
    		order.addToPurchasedItems(orderItem);
    	}
    	order.setTotalPrice(totalPrice);
    	order.setUser(user);
    	cartService.removeCartFromUser(cart);
    	orderRepository.save(order);
    	return order;
    	
    	//System.out.println("Added: " + mediaPurchasedRepository.findByOrder(order));
    	//orderRepository.delete(order);
    	//System.out.println("Delete: " + mediaPurchasedRepository.findByOrder(order));
    	
    }*/
    
    private OrderDto convertOrderToDto(Order order) {
    	OrderDto orderDto = new OrderDto();
    	orderDto.setId(order.getId());
    	orderDto.setPurchasedDate(order.getPurchasedDate());
    	orderDto.setTotalPrice(order.getTotalPrice());
    	List<MoviePurchased> moviePurchasedList = order.getPurchasedItems();
    	List<MoviePurchasedDto> moviePurchasedDtoList = new ArrayList<>();
    	if(moviePurchasedList != null)
    		for(MoviePurchased moviePurchased : moviePurchasedList) if(moviePurchased != null) {
    			MoviePurchasedDto moviePurchasedDto = MoviePurchasedService.convertMoviePurchasedtoDto(moviePurchased);
    			moviePurchasedDtoList.add(moviePurchasedDto);
    		}
    	orderDto.setPurchasedItems(moviePurchasedDtoList);
    	return orderDto;
    }
    
    private List<Order> getOrdersMadeBuyUser(User user) {
    	return orderRepository.findByUser(user).orElseThrow(null);
    }
	
}
