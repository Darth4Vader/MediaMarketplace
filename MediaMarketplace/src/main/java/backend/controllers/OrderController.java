package backend.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.dto.cart.AddProductToCartDto;
import backend.entities.Cart;
import backend.entities.CartProduct;
import backend.entities.MediaGenre;
import backend.entities.MediaProduct;
import backend.entities.MediaPurchased;
import backend.entities.Order;
import backend.entities.User;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.UserRepository;
import backend.services.CartService;
import backend.services.MediaGenreService;
import backend.services.OrderService;
import backend.services.TokenService;
import backend.services.UserServiceImpl;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {

	@Autowired
	private OrderService orderService; 
	
	@Autowired
	private TokenService tokenService;
	
	@GetMapping("/get/orders")
	public List<Order> getUserOrders() {
		User user = tokenService.getCurretUser();
		return orderService.getUserOrders(user);
    }
	
	@GetMapping("/place_order")
    public void placeOrder() {
		User user = tokenService.getCurretUser();
		orderService.placeOrder(user);
    }
	
	/*@GetMapping("/add")
    public ResponseEntity<String> addProductToCart(AddProductToCartDto dto) throws EntityNotFoundException, EntityAlreadyExistsException  {
		//String userName = tokenService.getCurrentUserName(token);
		//User user = userService.getUserByUserName(userName);
		User user = tokenService.getCurretUser();
		cartService.addProductToCart(dto, user);
		//List<MediaGenre> body = cartService.
        return new ResponseEntity<>("Added Succssesfully", HttpStatus.OK);
    }*/
	
	/*@GetMapping("/remove")
    public ResponseEntity<String> removeProductFromCart(AddProductToCartDto dto) throws EntityNotFoundException {
		//String userName = tokenService.getCurrentUserName(token);
		//User user = userService.getUserByUserName(userName);
		User user = tokenService.getCurretUser();
		cartService.removeProductFromCart(dto, user);
		//List<MediaGenre> body = cartService.
        return new ResponseEntity<>("Added Succssesfully", HttpStatus.OK);
    }*/
}
