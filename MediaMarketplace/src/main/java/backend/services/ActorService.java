package backend.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.auth.AuthenticateAdmin;
import backend.dto.input.ActorReference;
import backend.dto.mediaProduct.ActorDto;
import backend.entities.Actor;
import backend.entities.Movie;
import backend.entities.Person;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.ActorRepository;

/**
 * Service class for managing actors.
 * This class provides methods to manage actors in the context of movies,
 * including retrieving and adding actors to the database.
 * this is the business side of the spring application, where we do all of the logic operation for the actors.
 */
@Service
public class ActorService {
	
    @Autowired
    private ActorRepository actorRepository;
    
    @Autowired
    private PersonService personService;
    
    @Autowired
    private MovieService movieService;
    
    /**
     * Retrieves a list of actors dto objects for a given movie.
     * This method is accessible to every user (logged or not logged) and provides information about the actors of a specific movie.
     * 
     * @param movieId The ID of the movie for which actors are to be retrieved.
     * @return A list of ActorDto objects representing the actors of the specified movie.
     * @throws EntityNotFoundException if the movie with the given ID does not exist.
     */
    public List<ActorDto> getActorsOfMovie(Long movieId) throws EntityNotFoundException {
    	//we will get the entities of all the actors of the movie
    	Movie movie = movieService.getMovieByID(movieId);
    	List<Actor> actors = movie.getActorsRoles();
    	//and then convert them to dtos
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
    
    /**
     * Adds a new actor to the database.
     * This method is restricted to admin users and adds an actor to a specific movie.
     * 
     * @param actorReference The ActorReference object containing the details of the actor to be added.
     * @throws EntityNotFoundException if the person or movie specified in the actorReference does not exist.
     * @throws EntityAlreadyExistsException if the person is already an actor of the specified movie.
     */
    @AuthenticateAdmin
    @Transactional
    public void addActorRole(ActorReference actorReference) throws EntityNotFoundException, EntityAlreadyExistsException {
    	Person person = personService.getPersonByNameID(actorReference.getPersonMediaID());
    	Movie movie = movieService.getMovieByNameID(actorReference.getMovieMediaId());
    	try {
    		//if the actor exists, then he can't be added
    		getActorByMovie(movie.getId(), person.getId());
	    	throw new EntityAlreadyExistsException("The person \"" + person.getName() + "\" is already an actor in the movie");
    	}
    	catch (EntityNotFoundException e) {}
    	Actor actor = new Actor();
    	actor.setRoleName(actorReference.getRoleName());
    	actor.setPerson(person);
    	actor.setMedia(movie);
    	//we will save the actor
    	actor = actorRepository.save(actor);
    	List<Actor> actors = movie.getActorsRoles();
    	actors.add(actor);
    }
    
    /**
     * Removes an actor from the database.
     * This method is restricted to admin users and removes an actor from a specific movie.
     * 
     * @param actorReference The ActorReference object containing the details of the actor to be removed.
     * @throws EntityNotFoundException if the person or movie specified in the actorReference does not exist, 
     * or if the person is not acting the movie.
     */
    @AuthenticateAdmin
    @Transactional
    public void removeActor(ActorReference actorReference) throws EntityNotFoundException {
    	Person person = personService.getPersonByNameID(actorReference.getPersonMediaID());
    	Movie movie = movieService.getMovieByNameID(actorReference.getMovieMediaId());
    	Actor actor = getActorByMovie(movie.getId(), person.getId());
    	List<Actor> actors = movie.getActorsRoles();
    	//first we will remove the actor from the movie
    	actors.remove(actor);
    	//and then we can remove him from the database
    	removeActor(actor);
    }
    
    /**
     * Removes all actors from a specific movie.
     * This method is restricted to admin users and removes all actors associated with a specific movie.
     * 
     * @param movieId The ID of the movie from which all actors are to be removed.
     * @throws EntityNotFoundException if the movie specified by the movieId does not exist.
     */
    @AuthenticateAdmin
    @Transactional
    public void removeAllActorsFromMovie(Long movieId) throws EntityNotFoundException {
    	Movie movie = movieService.getMovieByID(movieId);
    	List<Actor> actors = movie.getActorsRoles();
    	//first we remove all of the actors
    	if(actors != null) for(Actor actor : actors) {
			removeActor(actor);
		}
    	//and then we can remove clear the movie actors list.
		actors.clear();
    }
    
    /**
     * Retrieves an actor by a movie ID and a person ID.
     * 
     * @param movieId The ID of the movie.
     * @param personId The ID of the person.
     * @return The Actor entity.
     * @throws EntityNotFoundException if the person is not acting the movie.
     */
    private Actor getActorByMovie(Long movieId, Long personId) throws EntityNotFoundException {
    	return actorRepository.findByMovieIdAndPersonId(movieId, personId)
    			.orElseThrow(() -> new EntityNotFoundException("The person \"" + personId + "\" is not an actor in the movie"));
    }
    
    /**
     * Helper method to remove an actor.
     * 
     * @param actor The actor to be removed.
     */
    private void removeActor(Actor actor) {
		actorRepository.delete(actor);
    } 
}