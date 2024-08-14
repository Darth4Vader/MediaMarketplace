package backend.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.DataUtils;
import backend.dto.mediaProduct.MovieReviewDto;
import backend.dto.mediaProduct.MovieReviewReference;
import backend.entities.Movie;
import backend.entities.MovieReview;
import backend.entities.User;
import backend.exceptions.DtoValuesAreIncorrectException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.enums.MovieReviewTypes;
import backend.repositories.MovieReviewRepository;

@Service
public class MovieReviewService {
	
    @Autowired
    private MovieReviewRepository movieReviewRepository;
    
    @Autowired
    private MovieService movieService;
    
	@Autowired
	private TokenService tokenService;
    
    //a non log user can get this information
    public List<MovieReviewDto> getAllReviewOfMovie(Long movieId) throws EntityNotFoundException {
    	Movie movie = movieService.getMovieByID(movieId);
    	List<MovieReview> movieReviews = getAllReviewOfMovies(movie);
    	List<MovieReviewDto> movieReviewDtos = new ArrayList<>();
    	if(movieReviews != null)
    		for(MovieReview movieReview : movieReviews) if(movieReview != null) {
    			MovieReviewDto movieReviewDto = convertMovieReviewToDto(movieReview);
    			movieReviewDtos.add(movieReviewDto);
    		}
    	return movieReviewDtos;
    }
    
    public MovieReviewReference getMovieReviewOfUser(Long movieId) throws EntityNotFoundException  {
		User user = tokenService.getCurretUser();
    	Movie movie = movieService.getMovieByID(movieId);
    	return convertMovieReviewToReference(getMovieReviewFromMovieUser(movie, user));
    }
    
    @Transactional
    public void addMovieReviewOfUser(MovieReviewReference movieReviewRef) throws DtoValuesAreIncorrectException, EntityNotFoundException {
    	User user = tokenService.getCurretUser();
    	checkForExceptionReviews(movieReviewRef);
    	Movie movie = movieService.getMovieByID(movieReviewRef.getMovieId());
    	MovieReview movieReview;
    	try {
			movieReview = getMovieReviewFromMovieUser(movie, user);
		} catch (EntityNotFoundException e) {
			movieReview = new MovieReview();
			movieReview.setUser(user);
			movieReview.setMovie(movie);
		}
    	getMovieReviewFromDto(movieReview, movieReviewRef);
    	movieReviewRepository.save(movieReview);
    }
    
    @Transactional
    public void addMovieRatingOfUser(MovieReviewReference movieReviewRef) throws DtoValuesAreIncorrectException, EntityNotFoundException {
    	User user = tokenService.getCurretUser();
    	checkForExceptionRatings(movieReviewRef);
    	Movie movie = movieService.getMovieByID(movieReviewRef.getMovieId());
    	MovieReview movieReview;
    	try {
			movieReview = getMovieReviewFromMovieUser(movie, user);
		} catch (EntityNotFoundException e) {
			movieReview = new MovieReview();
			movieReview.setUser(user);
			movieReview.setMovie(movie);
		}
    	getMovieRatingFromDto(movieReview, movieReviewRef);
    	movieReviewRepository.save(movieReview);
    }
    
    //a non log user can get this information
    public Integer getMovieRatings(Long movieId) {
    	List<MovieReview> movieReviews;
		try {
			Movie movie = movieService.getMovieByID(movieId);
			movieReviews = getAllReviewOfMovies(movie);
		} catch (EntityNotFoundException e) {
			//if there are no reviews of the movie then we will return a null.
			return null;
		}
    	double size = movieReviews.size();
    	double sum = 0;
    	for(MovieReview review : movieReviews) {
    		sum += review.getRating();
    	}
    	return (int) (sum / size);
    }
    
    private List<MovieReview> getAllReviewOfMovies(Movie movie) throws EntityNotFoundException{
    	return movieReviewRepository.findAllByMovie(movie)
    			.orElseThrow(() -> new EntityNotFoundException("There are no reviews of the given movie"));
    }
    
    private MovieReviewDto convertMovieReviewToDto(MovieReview movieReview) {
    	MovieReviewDto movieReviewDto = new MovieReviewDto();
    	MovieReviewReference movieReviewRef = convertMovieReviewToReference(movieReview);
    	movieReviewDto.setMovieReview(movieReviewRef);
    	User user = movieReview.getUser();
    	movieReviewDto.setUserName(user.getUsername());
    	return movieReviewDto;
    }
    
    private MovieReviewReference convertMovieReviewToReference(MovieReview movieReview) {
    	MovieReviewReference movieReviewRef = new MovieReviewReference();
    	movieReviewRef.setMovieId(movieReview.getMovie().getId());
    	movieReviewRef.setReview(movieReview.getReview());
    	movieReviewRef.setCreatedDate(movieReview.getCreatedDate());
    	movieReviewRef.setReviewTitle(movieReview.getReviewTitle());
    	movieReviewRef.setRating(movieReview.getRating());
    	return movieReviewRef;
    }
    
    private void getMovieRatingFromDto(MovieReview movieReview, MovieReviewReference movieReviewRef) {
    	movieReview.setRating(movieReviewRef.getRating());
    }
    
    private void getMovieReviewFromDto(MovieReview movieReview, MovieReviewReference movieReviewRef) {
    	getMovieRatingFromDto(movieReview, movieReviewRef);
    	movieReview.setReviewTitle(movieReviewRef.getReviewTitle());
    	movieReview.setReview(movieReviewRef.getReview());
    }
    
	private MovieReview getMovieReviewFromMovieUser(Movie movie, User user) throws EntityNotFoundException {
    	return movieReviewRepository.findByMovieAndUser(movie, user)
    			.orElseThrow(() -> new EntityNotFoundException("The user did not review the movie"));
	}
    
	private void checkForExceptionRatings(MovieReviewReference movieReviewRef) throws DtoValuesAreIncorrectException {
		Map<String, String> map = new HashMap<>();
		exceptionMapRatings(movieReviewRef, map);
		if(!map.isEmpty())
			throw new DtoValuesAreIncorrectException(map);
	}
	
	private void checkForExceptionReviews(MovieReviewReference movieReviewRef) throws DtoValuesAreIncorrectException {
		Map<String, String> map = new HashMap<>();
		exceptionMapReview(movieReviewRef, map);
		if(!map.isEmpty())
			throw new DtoValuesAreIncorrectException(map);
	}
	
	private void exceptionMapRatings(MovieReviewReference movieReviewRef, Map<String, String> map) {
		Integer rating = movieReviewRef.getRating();
		if(rating == null)
			map.put(MovieReviewTypes.RATING.name(), "Required field, the user must rate the movie");
		else if(rating <= 0 || rating > 100)
			map.put(MovieReviewTypes.RATING.name(), "The rating number must be between 1-100");
	}
	
	private void exceptionMapReview(MovieReviewReference movieReviewRef, Map<String, String> map) {
		exceptionMapRatings(movieReviewRef, map);
		String reviewTitle = movieReviewRef.getReviewTitle();
		String review = movieReviewRef.getReview();
		if(DataUtils.isBlank(reviewTitle))
			map.put(MovieReviewTypes.TITLE.name(), "Required field, the title must be written");
		else if(reviewTitle.length() > 255)
			map.put(MovieReviewTypes.REVIEW.name(), "The Review Title length must be less than 255");
		if(DataUtils.isNotBlank(review) && review.length() > 1000)
			map.put(MovieReviewTypes.REVIEW.name(), "The Review Content length must be less than 1000");
	}
}
