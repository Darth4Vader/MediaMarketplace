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
import backend.repositories.MoviePurchasedRepository;
import backend.repositories.OrderRepository;

@Service
public class MediaPurchasedService {
	
	@Autowired
	private MoviePurchasedRepository moviePurchasedRepository;
	
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
	
}
