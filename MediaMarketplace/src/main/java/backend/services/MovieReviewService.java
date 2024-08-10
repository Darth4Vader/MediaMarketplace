package backend.services;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import DataStructures.UserLogInfo;
import backend.DataUtils;
import backend.dto.mediaProduct.MovieReviewDto;
import backend.dto.mediaProduct.ProductDto;
import backend.entities.Genre;
import backend.entities.Movie;
import backend.entities.MovieReview;
import backend.entities.Product;
import backend.entities.User;
import backend.exceptions.DtoValuesAreIncorrectException;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.enums.MovieReviewTypes;
import backend.repositories.MovieRepository;
import backend.repositories.MovieReviewRepository;
import backend.repositories.ProductRepository;

@Service
public class MovieReviewService {
	
    @Autowired
    private MovieReviewRepository movieReviewRepository;
    
    @Autowired
    private MovieService movieService;
    
    //a non log user can get this information
    public List<MovieReview> getAllReviewOfMovie(Long movieId) throws EntityNotFoundException {
    	Movie movie = movieService.getMovieByID(movieId);
    	return movieReviewRepository.findAllByMovie(movie)
    			.orElseThrow(() -> new EntityNotFoundException("There are no reviews of the given movie"));
    }
    
    public MovieReview getMovieReviewOfUser(Long movieId, User user) throws EntityNotFoundException  {
    	Movie movie = movieService.getMovieByID(movieId);
    	return getMovieReviewFromMovieUser(movie, user);
    }
    
    public void addMovieReviewOfUser(MovieReviewDto movieReviewDto, User user) throws DtoValuesAreIncorrectException, EntityNotFoundException {
    	checkForExceptionReviews(movieReviewDto);
    	Movie movie = movieService.getMovieByID(movieReviewDto.getMovieId());
    	MovieReview movieReview;
    	try {
			movieReview = getMovieReviewFromMovieUser(movie, user);
		} catch (EntityNotFoundException e) {
			movieReview = new MovieReview();
			movieReview.setUser(user);
			movieReview.setMovie(movie);
		}
    	getMovieReviewFromDto(movieReview, movieReviewDto);
    	movieReviewRepository.save(movieReview);
    }
    
    public void addMovieRatingOfUser(MovieReviewDto movieReviewDto, User user) throws DtoValuesAreIncorrectException, EntityNotFoundException {
    	checkForExceptionRatings(movieReviewDto);
    	Movie movie = movieService.getMovieByID(movieReviewDto.getMovieId());
    	MovieReview movieReview;
    	try {
			movieReview = getMovieReviewFromMovieUser(movie, user);
		} catch (EntityNotFoundException e) {
			movieReview = new MovieReview();
			movieReview.setUser(user);
			movieReview.setMovie(movie);
		}
    	getMovieRatingFromDto(movieReview, movieReviewDto);
    	movieReviewRepository.save(movieReview);
    }
    private void getMovieRatingFromDto(MovieReview movieReview, MovieReviewDto movieReviewDto) {
    	movieReview.setRating(movieReviewDto.getRating());
    }
    
    private void getMovieReviewFromDto(MovieReview movieReview, MovieReviewDto movieReviewDto) {
    	getMovieRatingFromDto(movieReview, movieReviewDto);
    	movieReview.setReviewTitle(movieReviewDto.getReviewTitle());
    	movieReview.setReview(movieReviewDto.getReview());
    }
    
	private MovieReview getMovieReviewFromMovieUser(Movie movie, User user) throws EntityNotFoundException {
    	return movieReviewRepository.findByMovieAndUser(movie, user)
    			.orElseThrow(() -> new EntityNotFoundException("The user did not review the movie"));
	}
    
	private void checkForExceptionRatings(MovieReviewDto movieReviewDto) throws DtoValuesAreIncorrectException {
		Map<String, String> map = new HashMap<>();
		exceptionMapRatings(movieReviewDto, map);
		if(!map.isEmpty())
			throw new DtoValuesAreIncorrectException(map);
	}
	
	private void checkForExceptionReviews(MovieReviewDto movieReviewDto) throws DtoValuesAreIncorrectException {
		Map<String, String> map = new HashMap<>();
		exceptionMapReview(movieReviewDto, map);
		if(!map.isEmpty())
			throw new DtoValuesAreIncorrectException(map);
	}
	
	private void exceptionMapRatings(MovieReviewDto movieReviewDto, Map<String, String> map) {
		Integer rating = movieReviewDto.getRating();
		if(rating == null)
			map.put(MovieReviewTypes.RATING.name(), "Required field, the user must rate the movie");
		else if(rating <= 0 || rating > 100)
			map.put(MovieReviewTypes.RATING.name(), "The rating number must be between 1-100");
	}
	
	private void exceptionMapReview(MovieReviewDto movieReviewDto, Map<String, String> map) {
		exceptionMapRatings(movieReviewDto, map);
		String reviewTitle = movieReviewDto.getReviewTitle();
		String review = movieReviewDto.getReview();
		if(DataUtils.isBlank(reviewTitle))
			map.put(MovieReviewTypes.TITLE.name(), "Required field, the title must be written");
		else if(reviewTitle.length() > 255)
			map.put(MovieReviewTypes.REVIEW.name(), "The Review Title length must be less than 255");
		if(DataUtils.isNotBlank(review) && review.length() > 1000)
			map.put(MovieReviewTypes.REVIEW.name(), "The Review Content length must be less than 1000");
	}
    
}
