package backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.exceptions.EntityAdditionException;
import backend.exceptions.EntityAlreadyExistsException;
import backend.services.GenreService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/genres")
public class GenreController {

	@Autowired
	private GenreService genreService; 
	
	@GetMapping("/")
    public List<String> getAllGenres() {
        List<String> body = genreService.getAllGenres();
        return body;
    }
	
	@GetMapping("/create")
    public ResponseEntity<String> createGenre(@Valid @RequestBody String genreName) throws EntityAlreadyExistsException {
		try {
			genreService.createGenre(genreName);
		}
		catch (DataAccessException e) {//we will catch the Transactional runtime exception, and throw it as a custom exception
			throw new EntityAdditionException("Unable to add the genre with the name: " + genreName, e);
		}
        return new ResponseEntity<>("Created Successfully", HttpStatus.OK);
    }
}
