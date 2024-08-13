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

import backend.dto.cart.CartProductReference;
import backend.dto.mediaProduct.MoviePurchasedDto;
import backend.dto.mediaProduct.MovieReference;
import backend.entities.Cart;
import backend.entities.CartProduct;
import backend.entities.Genre;
import backend.entities.Movie;
import backend.entities.MoviePurchased;
import backend.entities.Order;
import backend.entities.User;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.UserNotLoggedInException;
import backend.repositories.UserRepository;
import backend.services.CartService;
import backend.services.GenreService;
import backend.services.MoviePurchasedService;
import backend.services.MovieService;
import backend.services.OrderService;
import backend.services.TokenService;
import backend.services.UserServiceImpl;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/movie_purchased")
public class MoviePurchasedController {

	@Autowired
	private MoviePurchasedService moviePurchasedService;
	
	@GetMapping("/get/user_media_products")
	public List<MovieReference> getAllActiveMediaProductsOfUser() {
		return moviePurchasedService.getAllActiveMoviesOfUser();
    }
	
	@GetMapping("/get_active/{movieId}")
    public List<MoviePurchasedDto> getActiveListUserMovie(@PathVariable Long movieId) throws EntityNotFoundException {
    	return moviePurchasedService.getActiveListUserMovie(movieId);
    }
}
