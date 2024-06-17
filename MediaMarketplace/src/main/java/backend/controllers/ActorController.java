package backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.entities.Actor;
import backend.entities.MediaGenre;
import backend.entities.User;
import backend.exceptions.EntityAlreadyExistsException;
import backend.services.ActorService;
import backend.services.MediaGenreService;
import backend.services.UserServiceImpl;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/actors")
public class ActorController {

	@Autowired
	private ActorService actorService; 
	
	@GetMapping("/")
    public List<Actor> getAllActors() {
		return actorService.getAllActors();
    }
	
	/*@GetMapping("/create")
    public ResponseEntity<String> createGenre(@Valid @RequestBody MediaGenre genre) throws EntityAlreadyExistsException {
		mediaGenreService.createGenre(genre);
        return new ResponseEntity<>("Created Successfully", HttpStatus.OK);
    }*/
}
