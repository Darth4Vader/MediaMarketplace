package backend.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backend.DataUtils;
import backend.dto.mediaProduct.MoviePurchasedDto;
import backend.dto.mediaProduct.MovieReference;
import backend.entities.Movie;
import backend.entities.MoviePurchased;
import backend.entities.User;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.MoviePurchasedRepository;

/**
 * Service class for managing purchased movies.
 * This class provides methods to retrieve information about movies purchased by users,
 * including checking the status of rentals and converting entities to DTOs.
 * this is the business side of the spring application, where we do all of the logic operation for the movies purchased.
 */
@Service
public class MoviePurchasedService {

    @Autowired
    private MoviePurchasedRepository moviePurchasedRepository;

    @Autowired
    private MovieService movieService;

    @Autowired
    private TokenService tokenService;

    /**
     * Retrieves a list of all active movies purchased by the current user.
     * A movie is considered active if it is rented and the rental period has not expired, 
     * or if it is bought (owned by the user).
     *
     * @return A list of MovieReference objects representing active movies.
     */
    public List<MovieReference> getAllActiveMoviesOfUser() {
        //first load all the movies purchased by the user
    	User user = tokenService.getCurretUser();
        List<MoviePurchased> purchasedList = moviePurchasedRepository.findByOrderUser(user);
        //then convert the activate ones to movie references.
        List<MovieReference> movieReferences = new ArrayList<>();
        for (MoviePurchased purchased : purchasedList) {
        	//checks if rent is still active or if it is bought.
            if (DataUtils.isUseable(purchased.isRented(), getCurrentRentTime(purchased))) {
                //adds the movie if it does not already contained in the list.
            	Movie movie = purchased.getMovie();
                MovieReference movieReference = MovieService.convertMovieToReference(movie);
                if (!movieReferences.contains(movieReference)) {
                    movieReferences.add(movieReference);
                }
            }
        }
        return movieReferences;
    }

    /**
     * Retrieves a list of active purchases for a specific movie by the current user.
     * A purchase is considered active if it is rented and the rental period has not expired, 
     * or if it is bought (owned by the user).
     *
     * @param movieId The ID of the movie for which active purchases are to be retrieved.
     * @return A list of MoviePurchasedDto objects representing active purchases of the specified movie.
     * @throws EntityNotFoundException if the movie with the given ID does not exist or if the user has never purchased the movie.
     */
    public List<MoviePurchasedDto> getActiveListUserMovie(Long movieId) throws EntityNotFoundException {
    	//first load all times the user purchased the given movie
    	User user = tokenService.getCurretUser();
        Movie movie = movieService.getMovieByID(movieId);
        List<MoviePurchased> purchasedList = getUserPurchaseListOfMovie(user, movie);
        //then convert the activate purchases to MoviePurchasedDto., and return them.
        List<MoviePurchasedDto> moviePurchasedDtos = new ArrayList<>();
        for (MoviePurchased purchased : purchasedList) {
            if (DataUtils.isUseable(purchased.isRented(), getCurrentRentTime(purchased))) {
                moviePurchasedDtos.add(convertMoviePurchasedtoDto(purchased));
            }
        }
        return moviePurchasedDtos;
    }

    /**
     * Converts a MoviePurchased entity to a MoviePurchasedDto.
     *
     * @param moviePurchased The MoviePurchased entity to convert.
     * @return A MoviePurchasedDto object representing the converted entity.
     */
    public static MoviePurchasedDto convertMoviePurchasedtoDto(MoviePurchased moviePurchased) {
        MoviePurchasedDto moviePurchasedDto = new MoviePurchasedDto();
        moviePurchasedDto.setId(moviePurchased.getId());
        moviePurchasedDto.setMovie(MovieService.convertMovieToReference(moviePurchased.getMovie()));
        moviePurchasedDto.setPurchasePrice(moviePurchased.getPurchasePrice());
        boolean isRented = moviePurchased.isRented();
        moviePurchasedDto.setRented(isRented);
        LocalDateTime purchaseDate = moviePurchased.getPurchaseDate();
        moviePurchasedDto.setPurchaseDate(purchaseDate);
        Duration rentTime = moviePurchased.getRentTime();
        moviePurchasedDto.setRentTime(rentTime);
        moviePurchasedDto.setRentTimeSincePurchase(getCurrentRentTime(isRented, purchaseDate, rentTime));
        return moviePurchasedDto;
    }

    /**
     * Retrieves the list of purchases for a specific movie made by a user.
     *
     * @param user  The user whose purchase list is to be retrieved.
     * @param movie The movie for which purchases are to be retrieved.
     * @return A list of MoviePurchased entities representing the user's purchases of the specified movie.
     * @throws EntityNotFoundException if the user has never purchased the movie.
     */
    private List<MoviePurchased> getUserPurchaseListOfMovie(User user, Movie movie) throws EntityNotFoundException {
        return moviePurchasedRepository.findAllByOrderUserAndMovie(user, movie)
                .filter(e -> !e.isEmpty())
                .orElseThrow(() -> new EntityNotFoundException("The user never purchased the movie"));
    }

    /**
     * Calculates the current rental expiration time for a given MoviePurchased entity.
     * This method delegates the calculation to a helper method that considers the rental status.
     *
     * @param moviePurchased The MoviePurchased entity.
     * @return The expiration time as a LocalDateTime, or null if not rented.
     */
    private static LocalDateTime getCurrentRentTime(MoviePurchased moviePurchased) {
        // Calls the helper method to calculate the current rental expiration time
        return getCurrentRentTime(moviePurchased.isRented(), moviePurchased.getPurchaseDate(), moviePurchased.getRentTime());
    }

    /**
     * Calculates the expiration time of a rental based on rental status and purchase date.
     *
     * @param isRented    Indicates if the movie is currently rented.
     * @param purchaseDate The date of purchase.
     * @param rentTime    The duration of the rental.
     * @return The expiration time as a LocalDateTime or null if not rented.
     */
    private static LocalDateTime getCurrentRentTime(boolean isRented, LocalDateTime purchaseDate, Duration rentTime) {
        if (!isRented) {
            return null;
        }
        return purchaseDate.plusSeconds(rentTime.getSeconds());
    }
}