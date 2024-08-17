package frontend.utils;

import java.util.ArrayList;
import java.util.List;

import backend.dtos.references.MovieReference;

/**
 * Represents a collection of movie references grouped in a single row.
 * <p>
 * This class maintains a list of {@link MovieReference} objects, allowing
 * for the management and manipulation of a set of movies that can be displayed
 * in a row format, such as in a grid or list view.
 * </p>
 */
public class MovieRow {

    private List<MovieReference> movies;

    /**
     * Constructs an empty {@code MovieRow} instance.
     * <p>
     * Initializes the {@code movies} list to be an empty {@code ArrayList}.
     * </p>
     */
    public MovieRow() {
        this.movies = new ArrayList<>();
    }

    /**
     * Constructs a {@code MovieRow} with a specified list of movies.
     * 
     * @param movies the list of {@code MovieReference} objects to initialize
     *               the {@code MovieRow} with
     */
    public MovieRow(List<MovieReference> movies) {
        super();
        this.movies = movies;
    }

    /**
     * Adds a movie to the row.
     * <p>
     * Appends the specified {@code MovieReference} to the internal list of
     * movies.
     * </p>
     * 
     * @param movie the {@code MovieReference} to be added to this row
     */
    public void add(MovieReference movie) {
        this.movies.add(movie);
    }

    /**
     * Retrieves the list of movies in this row.
     * 
     * @return a {@code List} of {@code MovieReference} objects representing
     *         the movies in this row
     */
    public List<MovieReference> getMovies() {
        return movies;
    }

    /**
     * Sets the list of movies for this row.
     * 
     * @param movies a {@code List} of {@code MovieReference} objects to set
     *               as the movies in this row
     */
    public void setMovies(List<MovieReference> movies) {
        this.movies = movies;
    }
}