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

import backend.entities.Genre;
import backend.entities.User;
import backend.exceptions.EntityAlreadyExistsException;
import backend.services.GenreService;
import backend.services.UserServiceImpl;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/genres")
public class GenreController {

	@Autowired
	private GenreService genreService; 
	
	@GetMapping("/")
    public List<Genre> getAllGenres() {
        List<Genre> body = genreService.getAllGenres();
        return body;
    }
	
	@GetMapping("/create")
    public ResponseEntity<String> createGenre(@Valid @RequestBody String genreName) throws EntityAlreadyExistsException {
		genreService.createGenre(genreName);
        return new ResponseEntity<>("Created Successfully", HttpStatus.OK);
    }
}
