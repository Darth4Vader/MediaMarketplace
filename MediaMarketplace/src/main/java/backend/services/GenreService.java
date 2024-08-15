package backend.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.auth.AuthenticateAdmin;
import backend.entities.Genre;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.EntityRemovalException;
import backend.repositories.GenreRepository;

/**
 * Service class for managing genres.
 * This class provides methods for retrieving, creating, and removing genres
 * in the context of a movie database. Access to certain methods is restricted
 * to admin users.
 * this is the business side of the spring application, where we do all of the logic operation for the genres.
 */
@Service
public class GenreService {

    @Autowired
    private GenreRepository genreRepository;

    /**
     * Retrieves a list of all genres.
     * This method is accessible to all users, including non-logged-in users.
     *
     * @return A list of genre names as strings.
     */
    public List<String> getAllGenres() {
        List<Genre> genres = genreRepository.findAll();
        return convertGenresToDto(genres);
    }

    /**
     * Creates a new genre in the database.
     * This method is restricted to admin users and checks if the genre
     * already exists before creating a new one.
     *
     * @param genreName The name of the genre to be created.
     * @throws EntityAlreadyExistsException if the genre with the specified name already exists.
     */
    @AuthenticateAdmin
    @Transactional
    public void createGenre(String genreName) throws EntityAlreadyExistsException {
        try {
            getGenreByName(genreName);
            throw new EntityAlreadyExistsException("The Genre with name: \"" + genreName + "\" already exists");
        } catch (EntityNotFoundException e) {
            // Genre does not exist; proceed to create it.
        }
        //if the genre does not exists, then save it to the database.
        Genre genre = new Genre(genreName);
        genreRepository.save(genre);
    }

    /**
     * Removes a genre from the database.
     * This method is restricted to admin users and checks if the genre
     * is associated with any movies before attempting to delete it.
     *
     * @param genreName The name of the genre to be removed.
     * @throws EntityNotFoundException if the genre with the specified name does not exist.
     * @throws EntityRemovalException if the genre cannot be removed due to associations with movies.
     */
    @AuthenticateAdmin
    @Transactional
    public void removeGenre(String genreName) throws EntityNotFoundException, EntityRemovalException {
        Genre genre = getGenreByName(genreName);
        if (genre.getMovies() == null) {
            try {
                genreRepository.delete(genre);
                return;
            } catch (Throwable e) {
                // Handle any exception during removal.
            }
        }
        throw new EntityRemovalException("Cannot remove the Genre with name: \"" + genreName + "\" because it is associated with movies.");
    }

    /**
     * Removes a genre from the database without a transactional context.
     * This method is restricted to admin users.
     *
     * @param genreName The name of the genre to be removed.
     * @throws EntityNotFoundException if the genre with the specified name does not exist.
     * @throws EntityRemovalException if the genre cannot be removed due to associations with movies.
     */
    @AuthenticateAdmin
    public void removeGenreWithoutTransactional(String genreName) throws EntityNotFoundException, EntityRemovalException {
        Genre genre = getGenreByName(genreName);
        try {
            genreRepository.delete(genre);
        } catch (Throwable e) {
            throw new EntityRemovalException("Cannot remove the Genre with name: \"" + genreName + "\".");
        }
    }

    /**
     * Converts a list of Genre entities to a list of genre names.
     *
     * @param genres The list of Genre entities to convert.
     * @return A list of genre names as strings.
     */
    public static List<String> convertGenresToDto(List<Genre> genres) {
        List<String> genresNameList = new ArrayList<>();
        if (genres != null) {
            for (Genre genre : genres) {
                genresNameList.add(genre.getName());
            }
        }
        return genresNameList;
    }

    /**
     * Retrieves a genre by its name.
     *
     * @param genreName The name of the genre to retrieve.
     * @return The {@link Genre} entity corresponding to the specified name.
     * @throws EntityNotFoundException if the genre with the specified name does not exist.
     */
    public Genre getGenreByName(String genreName) throws EntityNotFoundException {
        return genreRepository.findByName(genreName)
                .orElseThrow(() -> new EntityNotFoundException("The Genre with name: \"" + genreName + "\" does not exist"));
    }
}