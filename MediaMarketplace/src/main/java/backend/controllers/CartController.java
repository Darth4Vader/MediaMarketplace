package backend.controllers;

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
import backend.entities.Genre;
import backend.entities.User;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.UserRepository;
import backend.services.CartService;
import backend.services.MediaGenreService;
import backend.services.TokenService;
import backend.services.UserServiceImpl;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/cart")
public class CartController {

	@Autowired
	private CartService cartService; 
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private UserServiceImpl userService;
	
	@GetMapping("/get/cart_products")
	public List<CartProduct> getCartProducts() {
		User user = tokenService.getCurretUser();
		return cartService.getCartProducts(user);
    }
	
	@GetMapping("/add")
    public ResponseEntity<String> addProductToCart(AddProductToCartDto dto) throws EntityNotFoundException, EntityAlreadyExistsException  {
		//String userName = tokenService.getCurrentUserName(token);
		//User user = userService.getUserByUserName(userName);
		User user = tokenService.getCurretUser();
		cartService.addProductToCart(dto, user);
		//List<MediaGenre> body = cartService.
        return new ResponseEntity<>("Added Succssesfully", HttpStatus.OK);
    }
	
	@GetMapping("/remove")
    public ResponseEntity<String> removeProductFromCart(AddProductToCartDto dto) throws EntityNotFoundException {
		//String userName = tokenService.getCurrentUserName(token);
		//User user = userService.getUserByUserName(userName);
		User user = tokenService.getCurretUser();
		cartService.removeProductFromCart(dto, user);
		//List<MediaGenre> body = cartService.
        return new ResponseEntity<>("Added Succssesfully", HttpStatus.OK);
    }
	
	/*@GetMapping("/create")
    public ResponseEntity<String> createGenre(@Valid @RequestBody MediaGenre genre) throws EntityAlreadyExistsException {
		mediaGenreService.createGenre(genre);
        return new ResponseEntity<>("Created Successfully", HttpStatus.OK);
    }*/
}
