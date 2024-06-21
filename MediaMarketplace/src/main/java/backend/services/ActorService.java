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
import backend.repositories.GenreRepository;

@Service
public class ActorService {
	
    @Autowired
    private ActorRepository actorRepository;
    
    public List<Actor> getAllActors() {
    	return actorRepository.findAll();
    }
    
}
