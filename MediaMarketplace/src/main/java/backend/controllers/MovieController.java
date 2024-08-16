package backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import backend.dtos.CreateMovieDto;
import backend.dtos.MovieDto;
import backend.dtos.references.MovieReference;
import backend.exceptions.EntityAdditionException;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.services.MovieService;
import jakarta.validation.Valid;

/**
 * REST controller for managing movies in the system.
 * <p>
 * This controller provides endpoints for retrieving, adding, updating, and querying movies.
 * </p>
 */
@RestController
@RequestMapping("/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    /**
     * Retrieves all movies.
     * <p>
     * This endpoint returns a list of all movies available in the system.
     * </p>
     *
     * @return A list of {@link MovieReference} objects.
     */
    @GetMapping("/")
    @ResponseStatus(code = HttpStatus.OK)
    public List<MovieReference> getAllMovies() {
        return movieService.getAllMovies();
    }

    /**
     * Retrieves a specific movie by its ID.
     * <p>
     * This endpoint returns details of a movie identified by the given ID.
     * If the movie is not found, an {@link EntityNotFoundException} will be thrown.
     * </p>
     *
     * @param movieId The ID of the movie to retrieve.
     * @return A {@link MovieDto} object containing movie details.
     * @throws EntityNotFoundException If the movie with the specified ID does not exist.
     */
    @GetMapping("/get/{movieId}")
    public MovieDto getMovie(@PathVariable Long movieId) throws EntityNotFoundException {
        return movieService.getMovie(movieId);
    }
    
    /**
     * Retrieves the media ID of a specific movie.
     * <p>
     * This endpoint returns the media ID of a movie identified by the given ID.
     * If the movie is not found, an {@link EntityNotFoundException} will be thrown.
     * </p>
     *
     * @param movieId The ID of the movie whose media ID is to be retrieved.
     * @return The media ID of the movie as a {@link String}.
     * @throws EntityNotFoundException If the movie with the specified ID does not exist.
     */
    @GetMapping("/get_mediaID/{movieId}")
    public String getMovieMediaID(@PathVariable Long movieId) throws EntityNotFoundException {
        return movieService.getMovieMediaID(movieId);
    }

    /**
     * Adds a new movie.
     * <p>
     * This endpoint creates a new movie using the provided details. If there is a problem with adding the movie, such as a database constraint violation,
     * an {@link EntityAdditionException} will be thrown.
     * </p>
     *
     * @param createMovieDto The details of the movie to be added.
     * @return A {@link ResponseEntity} with a success message if the movie is added successfully.
     * @throws EntityAlreadyExistsException If a movie with the same media ID already exists.
     * @throws EntityNotFoundException If required entities for the movie do not exist.
     */
    @PostMapping("/add/{mediaID}")
    public ResponseEntity<String> addMovie(@Valid @RequestBody CreateMovieDto createMovieDto)
            throws EntityAlreadyExistsException, EntityNotFoundException {
        try {
            movieService.addMovie(createMovieDto);
        } catch (DataAccessException e) {
            throw new EntityAdditionException("Unable to add the movie with the media id: \"" + createMovieDto.getMediaID() + "\"", e);
        }
        return new ResponseEntity<>("Created Successfully", HttpStatus.OK);
    }

    /**
     * Updates an existing movie.
     * <p>
     * This endpoint updates a movie with the given details. If there is an issue with the update operation, such as a database constraint violation,
     * an {@link EntityAdditionException} will be thrown.
     * </p>
     *
     * @param createMovieDto The details of the movie to be updated.
     * @return The ID of the updated movie.
     * @throws EntityNotFoundException If the movie with the specified media ID does not exist.
     */
    @PostMapping("/update/{mediaID}")
    public Long updateMovie(@Valid @RequestBody CreateMovieDto createMovieDto) throws EntityNotFoundException {
        try {
            return movieService.updateMovie(createMovieDto);
        } catch (DataAccessException e) {
            throw new EntityAdditionException("Unable to update the movie with the media id: \"" + createMovieDto.getMediaID() + "\"", e);
        }
    }
}