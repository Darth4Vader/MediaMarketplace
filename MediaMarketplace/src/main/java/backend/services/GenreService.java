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
import backend.repositories.GenreRepository;

@Service
public class GenreService {
	
    @Autowired
    private GenreRepository genreRepository;
    
    public List<Genre> getAllGenres() {
    	return genreRepository.findAll();
    }
	
    public void createGenre(String genreName) throws EntityAlreadyExistsException {
    	try {	
    		getGenreByName(genreName);
    		throw new EntityAlreadyExistsException("The Genre with id: ("+genreName+") does already exists");
    	}
    	catch (EntityNotFoundException e) {}
    	Genre genre = new Genre(genreName);
    	genreRepository.save(genre);
    }
    
    public Genre getGenreByName(String genreName) throws EntityNotFoundException {
    	return genreRepository.findByName(genreName).
    			orElseThrow(() -> new EntityNotFoundException("The Genre with name: ("+genreName+") does not exists"));
    }
    
}
