package backend.services;

import java.time.Instant;
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

import backend.entities.Genre;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.MediaGenreRepository;

@Service
public class MediaGenreService {
	
    @Autowired
    private MediaGenreRepository mediaGenreRepository;
    
    public List<Genre> getAllGenres() {
    	return mediaGenreRepository.findAll();
    }
	
    public void createGenre(Genre genre) throws EntityAlreadyExistsException {
    	String name = genre.getName();
    	try {	
    		getGenreByName(name);
    		throw new EntityAlreadyExistsException("The Genre with id: ("+name+") does already exists");
    	}
    	catch (EntityNotFoundException e) {}
    	mediaGenreRepository.save(genre);
    }
    
    public Genre getGenreByName(String genreName) throws EntityNotFoundException {
    	return mediaGenreRepository.findByName(genreName).
    			orElseThrow(() -> new EntityNotFoundException("The Genre with name: ("+genreName+") does not exists"));
    }
    
}
