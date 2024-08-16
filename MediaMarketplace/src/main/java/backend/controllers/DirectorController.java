package backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.dtos.DirectorDto;
import backend.dtos.references.DirectorReference;
import backend.exceptions.EntityAdditionException;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.EntityRemovalException;
import backend.services.DirectorService;
import jakarta.validation.Valid;

/**
 * REST controller for managing directors in the system.
 * <p>
 * This controller provides endpoints for retrieving directors of a movie, adding directors, and removing directors from a movie.
 * </p>
 */
@RestController
@RequestMapping("/directors")
public class DirectorController {

    @Autowired
    private DirectorService directorService;

    /**
     * Retrieves the list of directors associated with a specified movie.
     * <p>
     * This method fetches the directors for the given movie ID.
     * </p>
     *
     * @param movieId The ID of the movie for which to retrieve directors.
     * @return A list of {@link DirectorDto} objects representing the directors of the specified movie.
     * @throws EntityNotFoundException If no directors are found for the given movie ID.
     */
    @GetMapping("/get/{movieId}")
    public List<DirectorDto> getDirectorsOfMovie(@PathVariable Long movieId) throws EntityNotFoundException {
        return directorService.getDirectorsOfMovie(movieId);
    }

    /**
     * Adds a director to a movie.
     * <p>
     * This endpoint adds a director to the movie specified in the request body. If there is an issue with adding the director,
     * such as a database constraint violation, an {@link EntityAdditionException} will be thrown.
     * </p>
     *
     * @param directorDto The {@link DirectorReference} containing the details of the director to be added.
     * @return A {@link ResponseEntity} with a success message if the director is added successfully.
     * @throws EntityNotFoundException If the specified movie or director is not found.
     * @throws EntityAlreadyExistsException If the director already exists for the specified movie.
     * @throws EntityAdditionException If there is a problem adding the director due to data access issues.
     */
    @PostMapping("/add/{movieMediaId}/{personMediaID}")
    public ResponseEntity<String> addDirector(@Valid @RequestBody DirectorReference directorDto)
            throws EntityNotFoundException, EntityAlreadyExistsException {
        try {
            directorService.addDirector(directorDto);
        } catch (DataAccessException e) {
            throw new EntityAdditionException("Unable to add the director to the movie", e);
        }
        return new ResponseEntity<>("Created Successfully", HttpStatus.OK);
    }

    /**
     * Removes a director from a movie.
     * <p>
     * This endpoint removes a director from the movie specified in the request body. If there is an issue with removing the director,
     * such as a database constraint violation, an {@link EntityRemovalException} will be thrown.
     * </p>
     *
     * @param directorDto The {@link DirectorReference} containing the details of the director to be removed.
     * @return A {@link ResponseEntity} with a success message if the director is removed successfully.
     * @throws EntityNotFoundException If the specified movie or director is not found.
     * @throws EntityRemovalException If there is a problem removing the director due to data access issues.
     */
    @DeleteMapping("/remove/{movieMediaId}/{personMediaID}")
    public ResponseEntity<String> removeDirector(@Valid @RequestBody DirectorReference directorDto)
            throws EntityNotFoundException {
        try {
            directorService.removeDirector(directorDto);
        } catch (DataAccessException e) {
            throw new EntityRemovalException("Unable to remove the director from the movie", e);
        }
        return new ResponseEntity<>("Removed Successfully", HttpStatus.OK);
    }

    /**
     * Removes all directors from a specified movie.
     * <p>
     * This endpoint removes all directors associated with the movie specified by the movie ID. If there is an issue with removing the
     * directors, such as a database constraint violation, an {@link EntityRemovalException} will be thrown.
     * </p>
     *
     * @param movieId The ID of the movie from which all directors are to be removed.
     * @return A {@link ResponseEntity} with a success message if all directors are removed successfully.
     * @throws EntityNotFoundException If the specified movie is not found.
     * @throws EntityRemovalException If there is a problem removing the directors due to data access issues.
     */
    @DeleteMapping("/remove_all_movie/{movieId}")
    public ResponseEntity<String> removeAllDirectorsFromMovie(@PathVariable Long movieId)
            throws EntityNotFoundException {
        try {
            directorService.removeAllDirectorsFromMovie(movieId);
        } catch (DataAccessException e) {
            throw new EntityRemovalException("Unable to remove all directors from the movie", e);
        }
        return new ResponseEntity<>("Removed Successfully", HttpStatus.OK);
    }
}