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

@Service
public class MoviePurchasedService {
	
	@Autowired
	private MoviePurchasedRepository moviePurchasedRepository;
	
	@Autowired
	private MovieService movieService;
	
	@Autowired
	private TokenService tokenService;
	
    public List<MovieReference> getAllActiveMoviesOfUser() {
    	User user = tokenService.getCurretUser();
    	List<MoviePurchased> purchasedList = moviePurchasedRepository.findByOrderUser(user);
    	List<MovieReference> movieReferences = new ArrayList<>();
    	for(MoviePurchased purchased : purchasedList) {
    		if(DataUtils.isUseable(purchased.isRented(), getCurrentRentTime(purchased))) {
    			Movie movie = purchased.getMovie();
    			MovieReference movieReference = MovieService.convertMovieToReference(movie);
    			if(!movieReferences.contains(movieReference)) {
    				movieReferences.add(movieReference);
    			}
    		}
    	}
    	return movieReferences;
    }
    
    public List<MoviePurchasedDto> getActiveListUserMovie(Long movieId) throws EntityNotFoundException {
    	User user = tokenService.getCurretUser();
    	Movie movie = movieService.getMovieByID(movieId);
    	List<MoviePurchased> purchasedList = getUserPurchaseListOfMovie(user, movie);
    	List<MoviePurchasedDto> moviePurchasedDtos = new ArrayList<>();
    	for(MoviePurchased purchased : purchasedList) {
    		if(DataUtils.isUseable(purchased.isRented(), getCurrentRentTime(purchased))) {
    			moviePurchasedDtos.add(convertMoviePurchasedtoDto(purchased));
    		}
    	}
    	return moviePurchasedDtos;
    }
    
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
    
    private List<MoviePurchased> getUserPurchaseListOfMovie(User user, Movie movie) throws EntityNotFoundException {
    	return moviePurchasedRepository.findAllByOrderUserAndMovie(user, movie)
    			.filter(e -> !e.isEmpty())
    			.orElseThrow(() -> new EntityNotFoundException("The user never purchased the movie"));
    }
	
	private static LocalDateTime getCurrentRentTime(MoviePurchased moviePurchased) {
    	LocalDateTime purchaseDate = moviePurchased.getPurchaseDate();
		Duration rentTime = moviePurchased.getRentTime();
		if(rentTime != null)
			return purchaseDate.plusSeconds(rentTime.getSeconds());;
		return null;
	}
	
	private static LocalDateTime getCurrentRentTime(boolean isRented, LocalDateTime purchaseDate, Duration rentTime) {
		if(!isRented)
			return null;
		return purchaseDate.plusSeconds(rentTime.getSeconds());
	}
	
}
