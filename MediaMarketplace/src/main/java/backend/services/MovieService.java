package backend.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import backend.dto.mediaProduct.MediaProductDto;
import backend.entities.Genre;
import backend.entities.Movie;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.MovieRepository;

@Service
public class MovieService {
	
    @Autowired
    private MovieRepository movieRepository;
    
    @Autowired
    private MediaGenreService mediaGenreService;
    
    public List<Movie> getAllMovies() {
    	return movieRepository.findAll();
    }
	
    public void addMediaProduct(MediaProductDto mediaDto) throws EntityAlreadyExistsException, EntityNotFoundException {
    	String mediaID = mediaDto.getMediaID();
    	try {	
    		getMediaByNameID(mediaID);
    		throw new EntityAlreadyExistsException("The MediaProduct with id: ("+mediaID+") does already exists");
    	}
    	catch (EntityNotFoundException e) {}
		List<String> genres = mediaDto.getGenresIDList();
		List<Genre> list = new ArrayList<>();
		for(String genre : genres) {
			try {
				list.add(mediaGenreService.getGenreByName(genre));
			}
    		catch (EntityNotFoundException e) {
    			throw new EntityNotFoundException("MediaProductService.addMediaProduct: The Genre with id: ("+genre+") does not exists");
			}
		}
		Movie mediaProduct = getProductFromDto(mediaDto, list);
    	movieRepository.save(mediaProduct);
    }
    
    public static Movie getProductFromDto(MediaProductDto mediaDto, List<Genre> genres) {
        Movie product = new Movie();
        product.setMediaID(mediaDto.getMediaID());
        product.setSynopsis(mediaDto.getSynopsis());
        product.setImagePath(mediaDto.getImagePath());
        //product.setPrice(mediaDto.getPrice());
        product.setName(mediaDto.getMediaName());
        product.setGenres(genres);
        return product;
    }
    
    public Movie getMediaByNameID(String mediaID) throws EntityNotFoundException {
    	return movieRepository.findByMediaID(mediaID).
    			orElseThrow(() -> new EntityNotFoundException("The MediaProduct with id: ("+mediaID+") does not exists"));
    }
    
    public Movie getMediaByID(Long id) throws EntityNotFoundException {
    	return movieRepository.findById(id).
    			orElseThrow(() -> new EntityNotFoundException("The MediaProduct with id: ("+id+") does not exists"));
    }
    
}
