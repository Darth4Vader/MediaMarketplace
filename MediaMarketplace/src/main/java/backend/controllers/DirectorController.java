package backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.dto.mediaProduct.ActorDto;
import backend.dto.mediaProduct.DirectorDto;
import backend.dto.mediaProduct.MovieDto;
import backend.entities.Actor;
import backend.entities.Director;
import backend.entities.Genre;
import backend.entities.Movie;
import backend.entities.Person;
import backend.entities.User;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.services.ActorService;
import backend.services.DirectorService;
import backend.services.GenreService;
import backend.services.UserServiceImpl;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/directors")
public class DirectorController {

	@Autowired
	private DirectorService directorService; 
	
	@GetMapping("/")
    public List<Director> getAllDirectors() {
		return directorService.getAllDirectors();
    }
	
	@GetMapping("/add")
    public ResponseEntity<String> addDirector(@Valid @RequestBody DirectorDto directorDto) throws EntityNotFoundException {
		directorService.addDirector(directorDto);
        return new ResponseEntity<>("Created Successfully", HttpStatus.OK);
	}
}
