package backend.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.auth.AuthenticateAdmin;
import backend.dto.input.DirectorReference;
import backend.dto.mediaProduct.DirectorDto;
import backend.entities.Director;
import backend.entities.Movie;
import backend.entities.Person;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.DirectorRepository;

/**
 * Service class for managing directors.
 * This class provides methods to manage directors in the context of movies,
 * including retrieving and adding directors to the database.
 * this is the business side of the spring application, where we do all of the logic operation for the directors.
 */
@Service
public class DirectorService {
	
    @Autowired
    private DirectorRepository directorRepository;
    
    @Autowired
    private PersonService personService;
    
    @Autowired
    private MovieService movieService;
    
    /**
     * Retrieves a list of directors dto objects for a given movie.
     * This method is accessible to every user (logged or not logged) and provides information about the directors of a specific movie.
     * 
     * @param movieId The ID of the movie for which directors are to be retrieved.
     * @return A list of DirectorDto objects representing the directors of the specified movie.
     * @throws EntityNotFoundException if the movie with the given ID does not exist.
     */
    public List<DirectorDto> getDirectorsOfMovie(Long movieId) throws EntityNotFoundException {
    	//we will get the entities of all the directors of the movie
    	Movie movie = movieService.getMovieByID(movieId);
    	List<Director> directors = movie.getDirectors();
    	//and then convert them to dtos
    	List<DirectorDto> directorsDto = new ArrayList<>();
    	if(directors != null) for(Director director : directors) {
    		DirectorDto directorDto = new DirectorDto();
    		directorDto.setMovieId(movieId);
    		directorDto.setPerson(PersonService.convertPersonToDto(director.getPerson()));
    		directorsDto.add(directorDto);
    	}
    	return directorsDto;
    }
    
    /**
     * Adds a new director to the database.
     * This method is restricted to admin users and adds a director to a specific movie.
     * 
     * @param directorReference The DirectorReference object containing the details of the director to be added.
     * @throws EntityNotFoundException if the person or movie specified in the directorReference does not exist.
     * @throws EntityAlreadyExistsException if the person is already a director of the specified movie.
     */
    @AuthenticateAdmin
    @Transactional
    public void addDirector(DirectorReference directorReference) throws EntityNotFoundException, EntityAlreadyExistsException {
    	Person person = personService.getPersonByNameID(directorReference.getPersonMediaID());
    	Movie movie = movieService.getMovieByNameID(directorReference.getMovieMediaId());
    	try {
    		//if the director exists, then he can't be added
    		getDirectorByMovie(movie.getId(), person.getId());
	    	throw new EntityAlreadyExistsException("The person \"" + person.getName() + "\" is already a director in the movie");
    	}
    	catch (EntityNotFoundException e) {}
    	Director director = new Director();
    	director.setPerson(person);
    	director.setMedia(movie);
    	//we will save the director
    	director = directorRepository.save(director);
    	//and now we add him to the movie
    	List<Director> directors = movie.getDirectors();
    	directors.add(director);
    }
    
    /**
     * Removes a director from the database.
     * This method is restricted to admin users and removes a director from a specific movie.
     * 
     * @param directorReference The DirectorReference object containing the details of the director to be removed.
     * @throws EntityNotFoundException if the person or movie specified in the directorReference does not exist, 
     * or if the person is not directing the movie.
     */
    @AuthenticateAdmin
    @Transactional
    public void removeDirector(DirectorReference directorReference) throws EntityNotFoundException {
    	Person person = personService.getPersonByNameID(directorReference.getPersonMediaID());
    	Movie movie = movieService.getMovieByNameID(directorReference.getMovieMediaId());
    	Director director = getDirectorByMovie(movie.getId(), person.getId());
    	List<Director> directors = movie.getDirectors();
    	//first we will remove the director from the movie
    	directors.remove(director);
    	//and then we can remove him from the database
    	removeDirector(director);
    }
    
    /**
     * Removes all directors from a specific movie.
     * This method is restricted to admin users and removes all directors associated with a specific movie.
     * 
     * @param movieId The ID of the movie from which all directors are to be removed.
     * @throws EntityNotFoundException if the movie specified by the movieId does not exist.
     */
    @AuthenticateAdmin
    @Transactional
    public void removeAllDirectorsFromMovie(Long movieId) throws EntityNotFoundException {
    	Movie movie = movieService.getMovieByID(movieId);
    	List<Director> directors = movie.getDirectors();
		//first we remove all of the directors
    	if(directors != null) for(Director director : directors) {
			removeDirector(director);
		}
    	//and then we can remove clear the movie directors list.
		directors.clear();
    }
    
    /**
     * Retrieves a director by a movie ID and a person ID.
     * 
     * @param movieId The ID of the movie.
     * @param personId The ID of the person.
     * @return The Director entity.
     * @throws EntityNotFoundException if the person is not directing the movie.
     */
    private Director getDirectorByMovie(Long movieId, Long personId) throws EntityNotFoundException {
    	return directorRepository.findByMovieIdAndPersonId(movieId, personId)
    			.orElseThrow(() -> new EntityNotFoundException("The person \"" + personId + "\" is not a director in the movie"));
    }
    
    /**
     * Helper method to remove a director.
     * 
     * @param director The director to be removed.
     */
    private void removeDirector(Director director) {
		directorRepository.delete(director);
    }
}