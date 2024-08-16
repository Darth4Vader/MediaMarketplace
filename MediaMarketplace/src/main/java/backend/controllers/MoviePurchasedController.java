package backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.dtos.MoviePurchasedDto;
import backend.dtos.references.MovieReference;
import backend.exceptions.EntityNotFoundException;
import backend.services.MoviePurchasedService;

/**
 * REST controller for managing purchased movies.
 * <p>
 * This controller provides endpoints for retrieving movies purchased by users.
 * </p>
 */
@RestController
@RequestMapping("/movie_purchased")
public class MoviePurchasedController {

    @Autowired
    private MoviePurchasedService moviePurchasedService;

    /**
     * Retrieves all active media products purchased by the user.
     * <p>
     * This endpoint returns a list of all active movies purchased by the user.
     * </p>
     *
     * @return A list of {@link MovieReference} objects representing active media products.
     */
    @GetMapping("/get/user_media_products")
    public List<MovieReference> getAllActiveMediaProductsOfUser() {
        return moviePurchasedService.getAllActiveMoviesOfUser();
    }

    /**
     * Retrieves a list of active user purchases for a specific movie.
     * <p>
     * This endpoint returns a list of active purchases for a movie identified by the given ID.
     * If the movie is not found, an {@link EntityNotFoundException} will be thrown.
     * </p>
     *
     * @param movieId The ID of the movie for which to retrieve active purchases.
     * @return A list of {@link MoviePurchasedDto} objects representing active purchases of the movie.
     * @throws EntityNotFoundException If the movie with the specified ID does not exist.
     */
    @GetMapping("/get_active/{movieId}")
    public List<MoviePurchasedDto> getActiveListUserMovie(@PathVariable Long movieId) throws EntityNotFoundException {
        return moviePurchasedService.getActiveListUserMovie(movieId);
    }
}