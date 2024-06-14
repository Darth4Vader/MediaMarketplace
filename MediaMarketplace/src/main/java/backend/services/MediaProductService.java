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
import backend.entities.MediaGenre;
import backend.entities.MediaProduct;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.MediaProductRepository;

@Service
public class MediaProductService {
	
    @Autowired
    private MediaProductRepository mediaProductRepository;
    
    @Autowired
    private MediaGenreService mediaGenreService;
    
    public List<MediaProduct> getAllMediaProducts() {
    	return mediaProductRepository.findAll();
    }
	
    public void addMediaProduct(MediaProductDto mediaDto) throws EntityAlreadyExistsException, EntityNotFoundException {
    	String mediaID = mediaDto.getMediaID();
    	try {	
    		getMediaByNameID(mediaID);
    		throw new EntityAlreadyExistsException("The MediaProduct with id: ("+mediaID+") does already exists");
    	}
    	catch (EntityNotFoundException e) {}
		List<String> genres = mediaDto.getGenresIDList();
		List<MediaGenre> list = new ArrayList<>();
		for(String genre : genres) {
			try {
				String genreID = MediaGenre.convertGenreNameToID(genre);
				list.add(mediaGenreService.getGenreByID(genreID));
			}
    		catch (EntityNotFoundException e) {
    			throw new EntityNotFoundException("MediaProductService.addMediaProduct: The Genre with id: ("+genre+") does not exists");
			}
		}
		MediaProduct mediaProduct = getProductFromDto(mediaDto, list);
    	mediaProductRepository.save(mediaProduct);
    }
    
    public static MediaProduct getProductFromDto(MediaProductDto mediaDto, List<MediaGenre> genres) {
        MediaProduct product = new MediaProduct();
        product.setMediaID(mediaDto.getMediaID());
        product.setSynopsis(mediaDto.getSynopsis());
        product.setImagePath(mediaDto.getImagePath());
        product.setPrice(mediaDto.getPrice());
        product.setMediaName(mediaDto.getMediaName());
        product.setGenres(genres);
        return product;
    }
    
    public MediaProduct getMediaByNameID(String mediaID) throws EntityNotFoundException {
    	return mediaProductRepository.findByMediaID(mediaID).
    			orElseThrow(() -> new EntityNotFoundException("The MediaProduct with id: ("+mediaID+") does not exists"));
    }
    
    public MediaProduct getMediaByID(Long id) throws EntityNotFoundException {
    	return mediaProductRepository.findById(id).
    			orElseThrow(() -> new EntityNotFoundException("The MediaProduct with id: ("+id+") does not exists"));
    }
    
}
