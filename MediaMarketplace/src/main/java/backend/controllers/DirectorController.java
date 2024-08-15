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

import backend.dto.input.DirectorReference;
import backend.dto.mediaProduct.DirectorDto;
import backend.dto.mediaProduct.MovieDto;
import backend.dto.mediaProduct.MovieReference;
import backend.exceptions.EntityAdditionException;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.EntityRemovalException;
import backend.services.DirectorService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/directors")
public class DirectorController {

	@Autowired
	private DirectorService directorService; 
	
	@GetMapping("/get/{movieId}")
    public List<DirectorDto> getDirectorsOfMovie(@PathVariable Long movieId) throws EntityNotFoundException {
		return directorService.getDirectorsOfMovie(movieId);
    }
	
	@GetMapping("/add")
    public ResponseEntity<String> addDirector(@Valid @RequestBody DirectorReference directorDto) throws EntityNotFoundException, EntityAlreadyExistsException {
		try {
			directorService.addDirector(directorDto);
		}
		catch (DataAccessException e) {//we will catch the Transactional runtime exception, and throw it as a custom exception
			throw new EntityAdditionException("Unable to add the director to the movie", e);
		}
        return new ResponseEntity<>("Created Successfully", HttpStatus.OK);
	}
	
	@GetMapping("/remove")
    public ResponseEntity<String> removeDirector(@Valid @RequestBody DirectorReference directorDto) throws EntityNotFoundException {
		try {
			directorService.removeDirector(directorDto);
		}
		catch (DataAccessException e) {//we will catch the Transactional runtime exception, and throw it as a custom exception
			throw new EntityRemovalException("Unable to remove the director from the movie", e);
		}
        return new ResponseEntity<>("Created Successfully", HttpStatus.OK);
	}
	
	@GetMapping("/remove_all")
    public ResponseEntity<String> removeAllDirectorsFromMovie(Long movieId) throws EntityNotFoundException {
		try {
			directorService.removeAllDirectorsFromMovie(movieId);
		}
		catch (DataAccessException e) {//we will catch the Transactional runtime exception, and throw it as a custom exception
			throw new EntityRemovalException("Unable to remove the director from the movie", e);
		}
		catch (Throwable e2) {//we will catch the Transactional runtime exception, and throw it as a custom exception
			e2.printStackTrace();
		}
        return new ResponseEntity<>("Created Successfully", HttpStatus.OK);
	}
}
