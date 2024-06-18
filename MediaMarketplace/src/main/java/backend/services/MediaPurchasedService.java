package backend.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.entities.Cart;
import backend.entities.CartProduct;
import backend.entities.MediaProduct;
import backend.entities.MediaPurchased;
import backend.entities.Order;
import backend.entities.User;
import backend.repositories.MediaPurchasedRepository;
import backend.repositories.OrderRepository;

@Service
public class MediaPurchasedService {
	
	@Autowired
	private MediaPurchasedRepository mediaPurchasedRepository;
	
    public List<MediaProduct> getAllActiveMediaProductsOfUser(User user) {
    	List<MediaPurchased> purchasedList = mediaPurchasedRepository.findByOrderUser(user);
    	System.out.println(purchasedList);
    	List<MediaProduct> mediaProducts = new ArrayList<>();
    	for(MediaPurchased purchased : purchasedList) {
    		if(purchased.isUseable()) {
    			MediaProduct media = purchased.getMediaProduct();
    			if(!mediaProducts.contains(media)) {
    				mediaProducts.add(media);
    			}
    		}
    	}
    	return mediaProducts;
    }
	
}
