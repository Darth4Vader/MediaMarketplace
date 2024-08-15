package backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.dto.input.ActorReference;
import backend.dto.mediaProduct.ActorDto;
import backend.dto.mediaProduct.MovieDto;
import backend.exceptions.EntityAdditionException;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.EntityRemovalException;
import backend.services.ActorService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/actors")
public class ActorController {

	@Autowired
	private ActorService actorService;
	
	@GetMapping("/get/{movieId}")
    public List<ActorDto> getActorsOfMovie(@PathVariable Long movieId) throws EntityNotFoundException {
		return actorService.getActorsOfMovie(movieId);
    }
	
	@GetMapping("/add ")
    public ResponseEntity<String> addActor(@Valid @RequestBody ActorReference actorDto) throws EntityNotFoundException, EntityAlreadyExistsException {
		try {
			actorService.addActorRole(actorDto);
		}
		catch (DataAccessException e) {//we will catch the Transactional runtime exception, and throw it as a custom exception
			throw new EntityAdditionException("Unable to create the actor", e);
		}
        return new ResponseEntity<>("Created Successfully", HttpStatus.OK);
	}
	
	@GetMapping("/remove")
    public ResponseEntity<String> removeActor(@Valid @RequestBody ActorReference actorDto) throws EntityNotFoundException {
		try {
			actorService.removeActor(actorDto);
		}
		catch (DataAccessException e) {//we will catch the Transactional runtime exception, and throw it as a custom exception
			throw new EntityRemovalException("Unable to remove the actor", e);
		}
        return new ResponseEntity<>("Created Successfully", HttpStatus.OK);
	}
	
	@GetMapping("/remove_all")
    public ResponseEntity<String> removeAllActorsFromMovie(Long movieId) throws EntityNotFoundException {
		try {
			actorService.removeAllActorsFromMovie(movieId);
		}
		catch (DataAccessException e) {//we will catch the Transactional runtime exception, and throw it as a custom exception
			throw new EntityRemovalException("Unable to remove all the actors", e);
		}
        return new ResponseEntity<>("Created Successfully", HttpStatus.OK);
	}
}
