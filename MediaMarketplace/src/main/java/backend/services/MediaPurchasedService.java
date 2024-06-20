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
import backend.repositories.MediaPurchasedRepository;
import backend.repositories.OrderRepository;

@Service
public class MediaPurchasedService {
	
	@Autowired
	private MediaPurchasedRepository mediaPurchasedRepository;
	
    public List<Movie> getAllActiveMediaProductsOfUser(User user) {
    	List<MoviePurchased> purchasedList = mediaPurchasedRepository.findByOrderUser(user);
    	System.out.println(purchasedList);
    	List<Movie> mediaProducts = new ArrayList<>();
    	for(MoviePurchased purchased : purchasedList) {
    		if(purchased.isUseable()) {
    			Movie media = purchased.getMovie();
    			if(!mediaProducts.contains(media)) {
    				mediaProducts.add(media);
    			}
    		}
    	}
    	return mediaProducts;
    }
	
}
