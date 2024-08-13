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
import org.springframework.transaction.annotation.Transactional;

import backend.auth.AuthenticateAdmin;
import backend.dto.input.RefActorDto;
import backend.dto.input.RefDirectorDto;
import backend.dto.mediaProduct.ActorDto;
import backend.dto.mediaProduct.DirectorDto;
import backend.dto.mediaProduct.MovieDto;
import backend.entities.Actor;
import backend.entities.Director;
import backend.entities.Genre;
import backend.entities.Movie;
import backend.entities.Person;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.ActorRepository;
import backend.repositories.GenreRepository;

@Service
public class ActorService {
	
    @Autowired
    private ActorRepository actorRepository;
    
    @Autowired
    private PersonService personService;
    
    @Autowired
    private MovieService movieService;
    
    /*//a non log user can get this information
    public List<Actor> getAllActors() {
    	return actorRepository.findAll();
    }*/
    
    //a non log user can get this information
    public List<ActorDto> getActorsOfMovie(Long movieId) throws EntityNotFoundException {
    	Movie movie = movieService.getMovieByID(movieId);
    	List<Actor> actors = movie.getActorsRoles();
    	List<ActorDto> actorsDto = new ArrayList<>();
    	if(actors != null) for(Actor actor : actors) {
    		ActorDto actorDto = new ActorDto();
    		actorDto.setMovieId(movieId);
    		actorDto.setPerson(PersonService.convertPersonToDto(actor.getPerson()));
    		actorDto.setRoleName(actor.getRoleName());
    		actorsDto.add(actorDto);
    	}
    	return actorsDto;
    }
    
    //only an admin can add an actor to the database
    @AuthenticateAdmin
    @Transactional
    public void addActorRole(RefActorDto actorDto) throws EntityNotFoundException, EntityAlreadyExistsException {
    	Person person = personService.getPersonByNameID(actorDto.getPersonMediaID());
    	Movie movie = movieService.getMovieByNameID(actorDto.getMovieMediaId());
    	try {
	    	getActorByMovie(movie.getId(), person.getId());
	    	throw new EntityAlreadyExistsException("The person is already an actor in the movie");
    	}
    	catch (EntityNotFoundException e) {}
    	Actor actor = new Actor();
    	actor.setRoleName(actorDto.getRoleName());
    	actor.setPerson(person);
    	actor.setMedia(movie);
    	actorRepository.save(actor);
    	List<Actor> actors = movie.getActorsRoles();
    	actors.add(actor);
    }
    
    //only an admin can remove an actor from the database
    @AuthenticateAdmin
    @Transactional
    public void removeActor(RefActorDto actorDto) throws EntityNotFoundException {
    	Person person = personService.getPersonByNameID(actorDto.getPersonMediaID());
    	Movie movie = movieService.getMovieByNameID(actorDto.getMovieMediaId());
    	Actor actor = getActorByMovie(movie.getId(), person.getId());
    	List<Actor> actors = movie.getActorsRoles();
    	actors.remove(actor);
    	removeActor(movie, actor);
    }
    
    //only an admin can remove all of the actors from the database
    @AuthenticateAdmin
    @Transactional
    public void removeAllActorsFromMovie(MovieDto movieDto) throws EntityNotFoundException {
    	Long movieId = movieDto.getId();
    	Movie movie = movieService.getMovieByID(movieId);
    	List<Actor> actors = movie.getActorsRoles();
		if(actors != null) for(Actor actor : actors) {
			removeActor(movie, actor);
		}
		actors.clear();
    }
    
    private void removeActor(Movie movie, Actor actor) {
		actorRepository.delete(actor);
    }
    
    /*@Transactional
    public void addActorRole(ActorDto actorDto, Movie movie) throws EntityNotFoundException {
    	Person person = personService.getPersonByNameID(actorDto.getPersonMediaID());
    	Actor actor = new Actor();
    	actor.setRoleName(actorDto.getRoleName());
    	actor.setPerson(person);
    	actor.setMedia(movie);
    	actorRepository.save(actor);
    	List<Actor> actors = movie.getActorsRoles();
    	actors.add(actor);
    }*/
    
    public Actor getActorByMovie(Long movieId, Long personId) throws EntityNotFoundException {
    	return actorRepository.findByMovieIdAndPersonId(movieId, personId)
    			.orElseThrow(() -> new EntityNotFoundException("The person is not an actor in the movie"));
    }
    
}
