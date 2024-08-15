package backend.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.auth.AuthenticateAdmin;
import backend.dto.mediaProduct.CreateMovieDto;
import backend.dto.mediaProduct.MovieDto;
import backend.dto.mediaProduct.MovieReference;
import backend.entities.Genre;
import backend.entities.Movie;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.MovieRepository;

/**
 * Service class for managing movies.
 * <p>
 * This class handles business logic related to movies within the application, including
 * retrieving movie details, adding new movies, updating existing movies, and handling
 * genre associations. It acts as an intermediary between the data access layer (repositories)
 * and the presentation layer (controllers), ensuring that all business rules and constraints
 * are enforced.
 * </p>
 */
@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;
    
    @Autowired
    private GenreService genreService;

    /**
     * Retrieves a list of all movies.
     * 
     * @return A list of MovieReference objects representing all movies.
     */
    public List<MovieReference> getAllMovies() {
    	//load all of the movies
        List<Movie> movies = movieRepository.findAll();
        //and then convert them into references.
        List<MovieReference> movieReferencesList = new ArrayList<>();
        for (Movie movie : movies) {
            MovieReference movieReference = convertMovieToReference(movie);
            movieReferencesList.add(movieReference);
        }
        return movieReferencesList;
    }

    /**
     * Retrieves details of a specific movie.
     * 
     * @param movieId The ID of the movie to retrieve.
     * @return A MovieDto object containing details of the specified movie.
     * @throws EntityNotFoundException if the movie with the specified ID does not exist.
     */
    public MovieDto getMovie(Long movieId) throws EntityNotFoundException {
        Movie movie = getMovieByID(movieId);
        return convertMovieToDto(movie);
    }

    /**
     * Adds a new movie to the database. 
     * Add the movie by his media id (String)
     * <p>This method is restricted to admin users only.</p>
     * 
     * @param createMovieDto The CreateMovieDto object containing details of the movie to add.
     * @throws EntityAlreadyExistsException if a movie with the same mediaID already exists.
     * @throws EntityNotFoundException if any of the genres specified do not exist.
     */
    @AuthenticateAdmin
    @Transactional
    public void addMovie(CreateMovieDto createMovieDto) throws EntityAlreadyExistsException, EntityNotFoundException {
        String mediaID = createMovieDto.getMediaID();
        try {
        	//load the movie from the database
            getMovieByNameID(mediaID);
            //if it is already in the database, then we already added it.
            throw new EntityAlreadyExistsException("The Movie with mediaId: (" + mediaID + ") already exists");
        } catch (EntityNotFoundException e) {
            // This exception is expected if the movie does not already exist.
        	//this needs to happen if we add a new movie into the database.
        }
        MovieDto movieDto = createMovieDto.getMovieDto();
        List<String> genresNames = movieDto.getGenres();
        List<Genre> genres = new ArrayList<>();
        //we find the new genres in the genres database
        for (String genreName : genresNames) {
            genres.add(genreService.getGenreByName(genreName));
        }
        //now we create the movie and save it into the database.
        Movie movie = new Movie();
        movie.setMediaID(mediaID);
        movie.setGenres(genres);
        updateMovieByDto(movie, movieDto);
        movieRepository.save(movie);
    }

    /**
     * Updates an existing movie in the database. 
     * Update the movie by him in the database using the media id (String). 
     * If the movie does not exists then we can't update it.
     * <p>This method is restricted to admin users only.</p>
     * 
     * @param createMovieDto The CreateMovieDto object containing updated details of the movie.
     * @return The ID of the updated movie.
     * @throws EntityNotFoundException if the movie with the specified mediaID does not exist.
     */
    @AuthenticateAdmin
    @Transactional
    public Long updateMovie(CreateMovieDto createMovieDto) throws EntityNotFoundException {
        //load the movie
    	String mediaID = createMovieDto.getMediaID();
        MovieDto movieDto = createMovieDto.getMovieDto();
        Movie movie = getMovieByNameID(mediaID);
        // Update the movie's genres only if there are new genres provided.
        List<String> genresNames = movieDto.getGenres();
        if (genresNames != null) {
            movie.setGenres(null); // Remove current genres
            List<Genre> newGenres = new ArrayList<>();
            for (String genreName : genresNames) {
                newGenres.add(genreService.getGenreByName(genreName));
            }
            movie.setGenres(newGenres); // Set new genres
        }
        //and save the movie
        updateMovieByDto(movie, movieDto);
        Movie updatedMovie = movieRepository.save(movie);
        return updatedMovie.getId();
    }

    /**
     * Retrieves the media ID of a specific movie.
     * <p>This method is restricted to admin users only.</p>
     * 
     * @param movieId The ID of the movie.
     * @return The media ID of the specified movie.
     * @throws EntityNotFoundException if the movie with the specified ID does not exist.
     */
    @AuthenticateAdmin
    public String getMovieMediaID(Long movieId) throws EntityNotFoundException {
        Movie movie = getMovieByID(movieId);
        return movie.getMediaID();
    }
    
    /**
     * Retrieves a Movie entity by its mediaID.
     * 
     * @param mediaID The media ID of the movie.
     * @return The Movie entity associated with the given media ID.
     * @throws EntityNotFoundException if no movie with the specified media ID exists.
     */
    public Movie getMovieByNameID(String mediaID) throws EntityNotFoundException {
        return movieRepository.findByMediaID(mediaID)
                .orElseThrow(() -> new EntityNotFoundException("The Movie with mediaId: (" + mediaID + ") does not exist"));
    }

    /**
     * Retrieves a Movie entity by its ID.
     * 
     * @param id The ID of the movie.
     * @return The Movie entity associated with the given ID.
     * @throws EntityNotFoundException if no movie with the specified ID exists.
     */
    public Movie getMovieByID(Long id) throws EntityNotFoundException {
        return movieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("The Movie with ID: (" + id + ") does not exist"));
    }

    /**
     * Converts a Movie entity to a MovieReference DTO.
     * 
     * @param movie The Movie entity to convert.
     * @return A MovieReference DTO representing the movie.
     */
    public static MovieReference convertMovieToReference(Movie movie) {
        MovieReference movieReference = new MovieReference();
        movieReference.setId(movie.getId());
        movieReference.setName(movie.getName());
        movieReference.setPosterPath(movie.getPosterPath());
        return movieReference;
    }

    /**
     * Converts a Movie entity to a MovieDto.
     * 
     * @param movie The Movie entity to convert.
     * @return A MovieDto object containing detailed information about the movie.
     */
    public static MovieDto convertMovieToDto(Movie movie) {
        MovieDto movieDto = new MovieDto();
        movieDto.setId(movie.getId());
        movieDto.setSynopsis(movie.getSynopsis());
        movieDto.setPosterPath(movie.getPosterPath());
        movieDto.setBackdropPath(movie.getBackdropPath());
        movieDto.setRuntime(movie.getRuntime());
        movieDto.setName(movie.getName());
        List<Genre> genres = movie.getGenres();
        List<String> genresNameList = GenreService.convertGenresToDto(genres);
        movieDto.setGenres(genresNameList);
        movieDto.setReleaseDate(movie.getReleaseDate());
        movieDto.setYear(movie.getYear());
        return movieDto;
    }

    /**
     * Updates the properties of a Movie entity based on the provided MovieDto. 
     * If a given field in the dto is a null, then don't update this field in the movie entity.
     * 
     * @param movie The Movie entity to update.
     * @param movieDto The MovieDto object containing updated details.
     */
    private static void updateMovieByDto(Movie movie, MovieDto movieDto) {
        String synopsis = movieDto.getSynopsis();
        if (synopsis != null) movie.setSynopsis(synopsis);
        String posterPath = movieDto.getPosterPath();
        if (posterPath != null) movie.setPosterPath(posterPath);
        String backdropPath = movieDto.getBackdropPath();
        if (backdropPath != null) movie.setBackdropPath(backdropPath);
        movie.setRuntime(movieDto.getRuntime());
        String name = movieDto.getName();
        if (name != null) movie.setName(name);
        movie.setReleaseDate(movieDto.getReleaseDate());
        movie.setYear(movieDto.getYear());
    }
}