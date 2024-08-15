package backend.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.DataUtils;
import backend.dto.mediaProduct.MovieRatingReference;
import backend.dto.mediaProduct.MovieReviewDto;
import backend.dto.mediaProduct.MovieReviewReference;
import backend.entities.Movie;
import backend.entities.MovieReview;
import backend.entities.User;
import backend.exceptions.DtoValuesAreIncorrectException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.enums.MovieReviewTypes;
import backend.repositories.MovieReviewRepository;

/**
 * Service class for managing movie reviews.
 * This class provides methods to retrieve, add, and validate reviews and ratings for movies,
 * as well as to convert entities to data transfer objects (DTOs).
 * this is the business side of the spring application, where we do all of the logic operation for the movie reviews.
 */
@Service
public class MovieReviewService {

    @Autowired
    private MovieReviewRepository movieReviewRepository;

    @Autowired
    private MovieService movieService;

    @Autowired
    private TokenService tokenService;

    /**
     * Retrieves all reviews for a specific movie.
     *
     * @param movieId The ID of the movie for which reviews are to be retrieved.
     * @return A list of MovieReviewDto objects containing reviews for the specified movie.
     * @throws EntityNotFoundException if the movie with the given ID does not exist, 
     * or if no reviews exist for the specified movie.
     */
    public List<MovieReviewDto> getAllReviewOfMovie(Long movieId) throws EntityNotFoundException {
        //first load of the movie reviews of the given movies.
    	Movie movie = movieService.getMovieByID(movieId);
        List<MovieReview> movieReviews = getAllReviewOfMovies(movie);
        //then convert them to dtos.
        List<MovieReviewDto> movieReviewDtos = new ArrayList<>();
        if (movieReviews != null) {
            for (MovieReview movieReview : movieReviews) {
                if (movieReview != null) {
                    MovieReviewDto movieReviewDto = convertMovieReviewToDto(movieReview);
                    movieReviewDtos.add(movieReviewDto);
                }
            }
        }
        return movieReviewDtos;
    }

    /**
     * Retrieves the review made by the current user for a specific movie.
     *
     * @param movieId The ID of the movie for which the user's review is to be retrieved.
     * @return A MovieReviewReference object containing the user's review details.
     * @throws EntityNotFoundException if the movie with the given ID does not exist or 
     *                                  if the user has not reviewed the movie.
     */
    public MovieReviewReference getMovieReviewOfUser(Long movieId) throws EntityNotFoundException {
        User user = tokenService.getCurretUser();
        Movie movie = movieService.getMovieByID(movieId);
        return convertMovieReviewToReference(getMovieReviewFromMovieUser(movie, user));
    }

    /**
     * Adds or updates a movie review for the current user.
     *
     * @param movieReviewRef The MovieReviewReference object containing review details.
     * @throws DtoValuesAreIncorrectException if the review details are invalid.
     * @throws EntityNotFoundException if the movie with the specified ID does not exist.
     */
    @Transactional
    public void addMovieReviewOfUser(MovieReviewReference movieReviewRef) 
            throws DtoValuesAreIncorrectException, EntityNotFoundException {
        //first we load the current user to verify that a logged user is trying to add a review.
    	User user = tokenService.getCurretUser();
    	//check that the format of the review details is correct, otherwise notify the user the problem with the input.
        checkForExceptionReviews(movieReviewRef);
        Movie movie = movieService.getMovieByID(movieReviewRef.getMovieId());
        MovieReview movieReview;
        try {
        	//try to load the current review by the user to the movie
            movieReview = getMovieReviewFromMovieUser(movie, user);
        } catch (EntityNotFoundException e) {
        	//if this is the first time the user review the movie, then we will create a new MovieReview for him.
            movieReview = new MovieReview();
            movieReview.setUser(user);
            movieReview.setMovie(movie);
        }
        //then we save the review.
        setMovieReviewFromDto(movieReview, movieReviewRef);
        movieReviewRepository.save(movieReview);
    }

    /**
     * Adds or updates a movie rating for the current user.
     *
     * @param movieRatingReference The MovieReviewReference object containing rating details.
     * @throws DtoValuesAreIncorrectException if the rating details are invalid.
     * @throws EntityNotFoundException if the movie with the specified ID does not exist.
     */
    @Transactional
    public void addMovieRatingOfUser(MovieRatingReference movieRatingReference) 
            throws DtoValuesAreIncorrectException, EntityNotFoundException {
    	//first we load the current user to verify that a logged user is trying to add a rating.
    	User user = tokenService.getCurretUser();
    	//check that the format of the ratings details is correct, otherwise notify the user the problem with the input.
    	checkForExceptionRatings(movieRatingReference);
        Movie movie = movieService.getMovieByID(movieRatingReference.getMovieId());
        MovieReview movieReview;
        try {
        	//try to load the current review by the user to the movie
        	movieReview = getMovieReviewFromMovieUser(movie, user);
        } catch (EntityNotFoundException e) {
        	//if this is the first time the user rate the movie, then we will create a new MovieReview for him.
        	movieReview = new MovieReview();
            movieReview.setUser(user);
            movieReview.setMovie(movie);
        }
        //then we save the review.
        setMovieRatingFromDto(movieReview, movieRatingReference);
        movieReviewRepository.save(movieReview);
    }

    /**
     * Calculates the average rating of a specific movie.
     *
     * @param movieId The ID of the movie for which the average rating is to be calculated.
     * @return The average rating of the movie, or null if no ratings exist.
     */
    public Integer getMovieRatings(Long movieId) {
        List<MovieReview> movieReviews;
        try {
        	//we load the movie and get all of his movie reviews.
            Movie movie = movieService.getMovieByID(movieId);
            movieReviews = getAllReviewOfMovies(movie);
        } catch (EntityNotFoundException e) {
            // If there are no reviews of the movie, return null.
            return null;
        }
        //now we calculate the average value of the sum. This is the movie ratings: total sum of ratings / number of times rated.
        double size = movieReviews.size();
        double sum = 0;
        for (MovieReview review : movieReviews) {
            sum += review.getRating();
        }
        return (int) (sum / size);
    }

    /**
     * Retrieves all reviews for a specified movie.
     *
     * @param movie The Movie entity for which reviews are to be retrieved.
     * @return A list of MovieReview entities for the specified movie.
     * @throws EntityNotFoundException if no reviews exist for the specified movie.
     */
    private List<MovieReview> getAllReviewOfMovies(Movie movie) throws EntityNotFoundException {
        return movieReviewRepository.findAllByMovie(movie)
                .orElseThrow(() -> new EntityNotFoundException("There are no reviews of the given movie"));
    }

    /**
     * Retrieves the review made by a specific user for a specific movie.
     *
     * @param movie The Movie entity.
     * @param user The User entity.
     * @return The MovieReview entity corresponding to the movie and user.
     * @throws EntityNotFoundException if the user has not reviewed the movie.
     */
    private MovieReview getMovieReviewFromMovieUser(Movie movie, User user) throws EntityNotFoundException {
        return movieReviewRepository.findByMovieAndUser(movie, user)
                .orElseThrow(() -> new EntityNotFoundException("The user did not review the movie"));
    }
    
    /**
     * Sets the rating from a MovieRatingReference to a MovieReview entity.
     *
     * @param movieReview The MovieReview entity to update.
     * @param movieRatingReference The MovieRatingReference containing the rating.
     */
    private void setMovieRatingFromDto(MovieReview movieReview, MovieRatingReference movieRatingReference) {
        movieReview.setRating(movieRatingReference.getRating());
    }

    /**
     * Updates a MovieReview entity based on the values from a MovieReviewReference.
     *
     * @param movieReview The MovieReview entity to update.
     * @param movieReviewRef The MovieReviewReference containing the new values.
     */
    private void setMovieReviewFromDto(MovieReview movieReview, MovieReviewReference movieReviewRef) {
        setMovieRatingFromDto(movieReview, movieReviewRef);
        movieReview.setReviewTitle(movieReviewRef.getReviewTitle());
        movieReview.setReview(movieReviewRef.getReview());
    }
    
    /**
     * Converts a MovieReview entity to a MovieReviewDto.
     *
     * @param movieReview The MovieReview entity to convert.
     * @return A MovieReviewDto object representing the converted entity.
     */
    private MovieReviewDto convertMovieReviewToDto(MovieReview movieReview) {
        MovieReviewDto movieReviewDto = new MovieReviewDto();
        MovieReviewReference movieReviewRef = convertMovieReviewToReference(movieReview);
        movieReviewDto.setMovieReview(movieReviewRef);
        User user = movieReview.getUser();
        movieReviewDto.setUserName(user.getUsername());
        return movieReviewDto;
    }

    /**
     * Converts a MovieReview entity to a MovieReviewReference.
     *
     * @param movieReview The MovieReview entity to convert.
     * @return A MovieReviewReference object representing the converted entity.
     */
    private MovieReviewReference convertMovieReviewToReference(MovieReview movieReview) {
        MovieReviewReference movieReviewRef = new MovieReviewReference();
        movieReviewRef.setMovieId(movieReview.getMovie().getId());
        movieReviewRef.setReview(movieReview.getReview());
        movieReviewRef.setCreatedDate(movieReview.getCreatedDate());
        movieReviewRef.setReviewTitle(movieReview.getReviewTitle());
        movieReviewRef.setRating(movieReview.getRating());
        return movieReviewRef;
    }

    /**
     * Validates the rating details in a MovieReviewReference object.
     *
     * @param movieRatingReference The MovieReviewReference containing rating details.
     * @throws DtoValuesAreIncorrectException if the rating details are invalid.
     */
    private void checkForExceptionRatings(MovieRatingReference movieRatingReference) throws DtoValuesAreIncorrectException {
        Map<String, String> map = new HashMap<>();
        exceptionMapRatings(movieRatingReference, map);
        if (!map.isEmpty())
            throw new DtoValuesAreIncorrectException(map);
    }

    /**
     * Validates the review details in a MovieReviewReference object.
     *
     * @param movieReviewRef The MovieReviewReference containing review details.
     * @throws DtoValuesAreIncorrectException if the review details are invalid.
     */
    private void checkForExceptionReviews(MovieReviewReference movieReviewRef) throws DtoValuesAreIncorrectException {
        Map<String, String> map = new HashMap<>();
        exceptionMapReview(movieReviewRef, map);
        if (!map.isEmpty())
            throw new DtoValuesAreIncorrectException(map);
    }
    
    /** Minimum value for a rating */
    private static final int RATING_MIN = 1;
    /** Maximum value for a rating */
    private static final int RATING_MAX = 100;
    /** Maximum length for a review title */
    private static final int REVIEW_TITLE_MAX_LENGTH = 255;
    /** Maximum length for a review */
    private static final int REVIEW_MAX_LENGTH = 1000;

    /**
     * Populates a map with validation errors related to movie ratings.
     *
     * @param movieRatingReference The MovieReviewReference containing rating details.
     * @param map The map to populate with validation error messages.
     */
    private void exceptionMapRatings(MovieRatingReference movieRatingReference, Map<String, String> map) {
        Integer rating = movieRatingReference.getRating();
        if (rating == null)
            map.put(MovieReviewTypes.RATING.name(), "Required field, the user must rate the movie");
        else if (rating <= RATING_MIN || rating > RATING_MAX)
            map.put(MovieReviewTypes.RATING.name(), String.format("The rating number must be between %d-%d", RATING_MIN, RATING_MAX));
    }

    /**
     * Populates a map with validation errors related to movie reviews.
     *
     * @param movieReviewRef The MovieReviewReference containing review details.
     * @param map The map to populate with validation error messages.
     */
    private void exceptionMapReview(MovieReviewReference movieReviewRef, Map<String, String> map) {
        exceptionMapRatings(movieReviewRef, map);
        String reviewTitle = movieReviewRef.getReviewTitle();
        String review = movieReviewRef.getReview();
        if (DataUtils.isBlank(reviewTitle))
            map.put(MovieReviewTypes.TITLE.name(), "Required field, the title must be written");
        else if (reviewTitle.length() > REVIEW_TITLE_MAX_LENGTH)
            map.put(MovieReviewTypes.REVIEW.name(), String.format("The Review Title length must be less than %d", REVIEW_TITLE_MAX_LENGTH));
        if (DataUtils.isNotBlank(review) && review.length() > REVIEW_MAX_LENGTH)
            map.put(MovieReviewTypes.REVIEW.name(), String.format("The Review Content length must be less than %d", REVIEW_MAX_LENGTH));
    }
}