package backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import backend.dto.mediaProduct.ProductDto;
import backend.entities.Movie;
import backend.entities.MovieReview;
import backend.entities.Product;
import backend.entities.User;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.services.MovieReviewService;
import backend.services.MovieService;
import backend.services.ProductService;
import backend.services.UserServiceImpl;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/movie_reviews")
public class MovieReviewController {

	@Autowired
	private MovieReviewService movieReviewService;
	
	@GetMapping("/get_all")
	@ResponseStatus(code = HttpStatus.OK)
    public List<MovieReview> getAllReviewOfMovie(Long movieId) throws EntityNotFoundException {
		return movieReviewService.getAllReviewOfMovie(movieId);
    }
	
	public static double calculateRating(List<MovieReview> reviews) {
		double size = reviews.size();
		double sum = 0;
		for(MovieReview review : reviews) {
			sum += review.getRating();
		}
		return sum / size;
	}
}
