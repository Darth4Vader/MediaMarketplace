package backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.dto.mediaProduct.MoviePurchasedDto;
import backend.dto.mediaProduct.MovieReference;
import backend.exceptions.EntityNotFoundException;
import backend.services.MoviePurchasedService;

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
