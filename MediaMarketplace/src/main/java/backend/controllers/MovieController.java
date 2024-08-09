package backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import backend.dto.mediaProduct.MovieDto;
import backend.entities.Movie;
import backend.entities.User;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.services.MovieService;
import backend.services.UserServiceImpl;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/movies")
public class MovieController {

	@Autowired
	private MovieService movieService; 
	
	@GetMapping("/")
	@ResponseStatus(code = HttpStatus.OK)
    public List<Movie> getAllMovies() {
        List<Movie> body = movieService.getAllMovies();
        return body;
        //return new ResponseEntity<>(body, HttpStatus.OK);
    }
	
	@GetMapping("/add")
    public ResponseEntity<String> addMovie(@Valid @RequestBody MovieDto movieDto) throws EntityAlreadyExistsException, EntityNotFoundException {
		movieService.addMovie(movieDto);
        return new ResponseEntity<>("Created Successfully", HttpStatus.OK);
    }
	
	@GetMapping("/update")
    public  ResponseEntity<String> updateMovie(@Valid @RequestBody MovieDto movieDto) throws EntityNotFoundException {
		movieService.updateMovie(movieDto);
		return new ResponseEntity<>("Created Successfully", HttpStatus.OK);
    }
}
