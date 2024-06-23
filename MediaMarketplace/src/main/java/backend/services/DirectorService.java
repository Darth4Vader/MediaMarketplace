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
import org.springframework.transaction.annotation.Transactional;

import backend.dto.mediaProduct.ActorDto;
import backend.dto.mediaProduct.DirectorDto;
import backend.entities.Actor;
import backend.entities.Director;
import backend.entities.Genre;
import backend.entities.Movie;
import backend.entities.Person;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.ActorRepository;
import backend.repositories.DirectorRepository;
import backend.repositories.GenreRepository;

@Service
public class DirectorService {
	
    @Autowired
    private DirectorRepository directorRepository;
    
    @Autowired
    private PersonService personService;
    
    @Autowired
    private MovieService movieService;
    
    public List<Director> getAllDirectors() {
    	return directorRepository.findAll();
    }
    
    @Transactional
    public void addDirector(DirectorDto directorDto) throws EntityNotFoundException {
    	Person person = personService.getPersonByNameID(directorDto.getPersonMediaID());
    	Movie movie = movieService.getMovieByNameID(directorDto.getMovieMediaId());
    	Director director = new Director();
    	director.setDirector(person);
    	director.setMedia(movie);
    	directorRepository.save(director);
    	List<Director> directors = movie.getDirectors();
    	directors.add(director);
    }
    
}
