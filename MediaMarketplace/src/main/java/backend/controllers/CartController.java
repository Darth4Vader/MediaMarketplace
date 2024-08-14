package backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.dto.cart.CartDto;
import backend.dto.cart.CartProductReference;
import backend.exceptions.EntityAdditionException;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.EntityRemovalException;
import backend.services.CartService;

@RestController
@RequestMapping("/carts")
public class CartController {

	@Autowired
	private CartService cartService;
	
	@GetMapping("/get")
	public CartDto getCart() throws EntityNotFoundException {
		return cartService.getCart();
    }
	
	@GetMapping("/add/{productId}")
    public ResponseEntity<String> addProductToCart(@PathVariable CartProductReference dto) throws EntityNotFoundException, EntityAlreadyExistsException  {
		try {
			cartService.addProductToCart(dto);
		}
		catch (DataAccessException e) {//we will catch the Transactional runtime exception, and throw it as a custom exception
			throw new EntityAdditionException("Unable to add the product \"" + dto.getProductId() + "\" to the carts", e);
		}
        return new ResponseEntity<>("Added Succssesfully", HttpStatus.OK);
    }
	
	@GetMapping("/remove/{productId}")
    public ResponseEntity<String> removeProductFromCart(@PathVariable CartProductReference dto) throws EntityNotFoundException {
		try {
			cartService.removeProductFromCart(dto);
		}
		catch (DataAccessException e) {//we will catch the Transactional runtime exception, and throw it as a custom exception
			throw new EntityRemovalException("Unable to remove the product \"" + dto.getProductId() + "\" from the carts", e);
		}
        return new ResponseEntity<>("Added Succssesfully", HttpStatus.OK);
    }
}
