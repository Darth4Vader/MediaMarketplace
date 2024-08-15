package backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import backend.dto.mediaProduct.MovieRatingReference;
import backend.dto.mediaProduct.MovieReviewDto;
import backend.dto.mediaProduct.MovieReviewReference;
import backend.exceptions.DtoValuesAreIncorrectException;
import backend.exceptions.EntityAdditionException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.UserNotLoggedInException;
import backend.services.MovieReviewService;

@RestController
@RequestMapping("/movie_reviews")
public class MovieReviewController {

	@Autowired
	private MovieReviewService movieReviewService;
	
	@GetMapping("/get_all/{movieId}")
	@ResponseStatus(code = HttpStatus.OK)
    public List<MovieReviewDto> getAllReviewOfMovie(Long movieId) throws EntityNotFoundException {
		return movieReviewService.getAllReviewOfMovie(movieId);
    }
	
	@GetMapping("/get/{movieId}/{userId}")
	@ResponseStatus(code = HttpStatus.OK)
    public MovieReviewReference getMovieReviewOfUser(Long movieId) throws EntityNotFoundException, UserNotLoggedInException {
		return movieReviewService.getMovieReviewOfUser(movieId);
    }
	
	@GetMapping("/add_review/{movieId}/{userId}")
	@ResponseStatus(code = HttpStatus.OK)
    public void addMovieReviewOfUser(MovieReviewReference movieReviewDto) throws DtoValuesAreIncorrectException, EntityNotFoundException {
		try {
			movieReviewService.addMovieReviewOfUser(movieReviewDto);
		}
		catch (DataAccessException e) {//we will catch the Transactional runtime exception, and throw it as a custom exception
			throw new EntityAdditionException("Unable to add user review to the movie \"" + movieReviewDto.getMovieId() + "\"", e);
		}
	}
	
	@GetMapping("/add_ratings/{movieId}/{userId}")
	@ResponseStatus(code = HttpStatus.OK)
    public void addMovieRatingOfUser(MovieRatingReference movieReviewDto) throws DtoValuesAreIncorrectException, EntityNotFoundException {
		try {
			movieReviewService.addMovieRatingOfUser(movieReviewDto);
		}
		catch (DataAccessException e) {//we will catch the Transactional runtime exception, and throw it as a custom exception
			throw new EntityAdditionException("Unable to add user ratings to the movie \"" + movieReviewDto.getMovieId() + "\"", e);
		}
    }
	
	@GetMapping("/get_ratings/{movieId}")
	@ResponseStatus(code = HttpStatus.OK)
    public Integer getMovieRatings(Long movieId) {
		return movieReviewService.getMovieRatings(movieId);
    }
}
