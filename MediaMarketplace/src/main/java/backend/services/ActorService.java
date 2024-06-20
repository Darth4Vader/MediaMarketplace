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

import backend.entities.Actor;
import backend.entities.Genre;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.ActorRepository;
import backend.repositories.MediaGenreRepository;

@Service
public class ActorService {
	
    @Autowired
    private ActorRepository actorRepository;
    
    public List<Actor> getAllActors() {
    	return actorRepository.findAll();
    }
	
    /*public void createGenre(MediaGenre genre) throws EntityAlreadyExistsException {
    	String genreID = genre.getGenreID();
    	try {	
    		getGenreByID(genreID);
    		throw new EntityAlreadyExistsException("The Genre with id: ("+genreID+") does already exists");
    	}
    	catch (EntityNotFoundException e) {}
    	mediaGenreRepository.save(genre);
    }
    
    public MediaGenre getGenreByID(String genreID) throws EntityNotFoundException {
    	return mediaGenreRepository.findByGenreID(genreID).
    			orElseThrow(() -> new EntityNotFoundException("The Genre with id: ("+genreID+") does not exists"));
    }*/
    
}
