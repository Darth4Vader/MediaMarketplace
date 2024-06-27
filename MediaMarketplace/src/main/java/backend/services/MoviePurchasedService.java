package backend.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.entities.Cart;
import backend.entities.CartProduct;
import backend.entities.Movie;
import backend.entities.MoviePurchased;
import backend.entities.Order;
import backend.entities.User;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.MoviePurchasedRepository;
import backend.repositories.OrderRepository;

@Service
public class MoviePurchasedService {
	
	@Autowired
	private MoviePurchasedRepository moviePurchasedRepository;
	
	@Autowired
	private MovieService movieService;
	
    public List<Movie> getAllActiveMoviesOfUser(User user) {
    	List<MoviePurchased> purchasedList = moviePurchasedRepository.findByOrderUser(user);
    	System.out.println(purchasedList);
    	List<Movie> movies = new ArrayList<>();
    	for(MoviePurchased purchased : purchasedList) {
    		if(purchased.isUseable()) {
    			Movie movie = purchased.getMovie();
    			if(!movies.contains(movie)) {
    				movies.add(movie);
    			}
    		}
    	}
    	return movies;
    }
    
    public List<MoviePurchased> getActiveListUserMovie(User user, Long movieId) throws EntityNotFoundException {
    	Movie movie = movieService.getMovieByID(movieId);
    	System.out.println("Full cracker story");
    	System.out.println(moviePurchasedRepository.findAllByOrderUserAndMovie(user, movie));
    	List<MoviePurchased> purchasedList = getUserPurchaseListOfMovie(user, movie);
    	List<MoviePurchased> movies = new ArrayList<>();
    	for(MoviePurchased purchased : purchasedList) {
    		if(purchased.isUseable()) {
    			movies.add(purchased);
    		}
    	}
    	return movies;
    }
    
    public List<MoviePurchased> getUserPurchaseListOfMovie(User user, Movie movie) throws EntityNotFoundException {
    	return moviePurchasedRepository.findAllByOrderUserAndMovie(user, movie)
    			.filter(e -> !e.isEmpty())
    			.orElseThrow(() -> new EntityNotFoundException("The user never purchased the movie"));
    }
	
}
