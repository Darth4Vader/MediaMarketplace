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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import backend.dto.mediaProduct.CreateMovieDto;
import backend.dto.mediaProduct.MovieDto;
import backend.dto.mediaProduct.MovieReference;
import backend.exceptions.EntityAdditionException;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.services.MovieService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/movies")
public class MovieController {

	@Autowired
	private MovieService movieService; 
	
	@GetMapping("/")
	@ResponseStatus(code = HttpStatus.OK)
    public List<MovieReference> getAllMovies() {
        List<MovieReference> body = movieService.getAllMovies();
        return body;
    }
	
	@GetMapping("/get/{movieId}")
    public  MovieDto getMovie(@PathVariable Long movieId) throws EntityNotFoundException {
		return movieService.getMovie(movieId);
    }
	
	@GetMapping("/add")
    public ResponseEntity<String> addMovie(@Valid @RequestBody CreateMovieDto createMovieDto) throws EntityAlreadyExistsException, EntityNotFoundException {
		try {
			movieService.addMovie(createMovieDto);
		}
		catch (DataAccessException e) {//we will catch the Transactional runtime exception, and throw it as a custom exception
			throw new EntityAdditionException("Unable to add the movie with the media id: \"" + createMovieDto.getMediaID() + "\"", e);
		}
        return new ResponseEntity<>("Created Successfully", HttpStatus.OK);
    }
	
	@GetMapping("/update")
    public Long updateMovie(@Valid @RequestBody CreateMovieDto createMovieDto) throws EntityNotFoundException {
		try {
			return movieService.updateMovie(createMovieDto);
		}
		catch (DataAccessException e) {//we will catch the Transactional runtime exception, and throw it as a custom exception
			throw new EntityAdditionException("Unable to update the movie with the media id: \"" + createMovieDto.getMediaID() + "\"", e);
		}
    }
	
	@GetMapping("/get/{movieId}/mediaId")
    public String getMovieMediaID(Long movieId) throws EntityNotFoundException {
		return movieService.getMovieMediaID(movieId);
    }
}
