package backend.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import backend.auth.AuthenticateAdmin;
import backend.dto.input.RefActorDto;
import backend.dto.mediaProduct.CreateMovieDto;
import backend.dto.mediaProduct.MovieDto;
import backend.dto.mediaProduct.MovieReference;
import backend.entities.Genre;
import backend.entities.Movie;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.EntityRemovalException;
import backend.repositories.MovieRepository;

@Service
public class MovieService {
	
    @Autowired
    private MovieRepository movieRepository;
    
    @Autowired
    private GenreService genreService;
    
    //a non log user can get this information
    public List<MovieReference> getAllMovies() {
    	List<Movie> movies = movieRepository.findAll();
    	List<MovieReference> moviesReference = new ArrayList<>();
    	for(Movie movie : movies) {
    		MovieReference movieReference = convertMovieToReference(movie);
    		moviesReference.add(movieReference);
    	}
    	return moviesReference;
    }
    
    //a non log user can get this information
    public MovieDto getMovie(Long movieId) throws EntityNotFoundException {
    	Movie movie = getMovieByID(movieId);
    	MovieDto movieDto = convertMovieToDto(movie);
    	return movieDto;
    }
	
    //only an admin can add a movie to the database
    @AuthenticateAdmin
    public void addMovie(CreateMovieDto createMovieDto) throws EntityAlreadyExistsException, EntityNotFoundException {
    	String mediaID = createMovieDto.getMediaID();
    	try {	
    		getMovieByNameID(mediaID);
    		throw new EntityAlreadyExistsException("The Movie with mediaId: ("+mediaID+") does already exists");
    	}
    	catch (EntityNotFoundException e) {} // this is what we expect to get, that's why we don't throw
		MovieDto movieDto = createMovieDto.getMovieDto();
    	List<String> genresNames = movieDto.getGenres();
		List<Genre> genres = new ArrayList<>();
		for(String genreName : genresNames)
			genres.add(genreService.getGenreByName(genreName));
		Movie movie = new Movie();
		movie.setMediaID(mediaID);
		movie.setGenres(genres);
		updateMovieByDto(movie, movieDto);
    	movieRepository.save(movie);
    }
    
    //only an admin can update a movie in the database
    @AuthenticateAdmin
    public void updateMovie(CreateMovieDto createMovieDto) throws EntityNotFoundException {
    	String mediaID = createMovieDto.getMediaID();
    	MovieDto movieDto = createMovieDto.getMovieDto();
    	Movie movie = getMovieByNameID(mediaID);
		
    	//we will replace the movie genre only if there is a new value to update them with
    	List<String> genresNames = movieDto.getGenres();
		if(genresNames != null) {
			List<Genre> currentGenre = movie.getGenres();
			//we removed the current genres of the movie
			movie.setGenres(null);
			//we add the new genres to the movie
			List<Genre> newGenres = new ArrayList<>();
			for(String genreName : genresNames)
				newGenres.add(genreService.getGenreByName(genreName));
			movie.setGenres(newGenres);
			//if the movie had before genres, than we try to remove the genres if possible from the database
			if(currentGenre != null)
				for(Genre genre : currentGenre) {
					try {
						genreService.removeGenre(genre.getName());
					} catch (EntityNotFoundException e) {
					} catch (EntityRemovalException e) {
					}
				}
		}
		updateMovieByDto(movie, movieDto);
    	movieRepository.save(movie);
    }
    
    //only an admin can get the movie id from the database (we don't want a non admin to see this value)
    @AuthenticateAdmin
    public String getMovieMediaID(Long movieId) throws EntityNotFoundException {
    	Movie movie = getMovieByID(movieId);
    	return movie.getMediaID();
    }
    
    public static MovieReference convertMovieToReference(Movie movie) {
        MovieReference movieReference = new MovieReference();
        movieReference.setId(movie.getId());
        movieReference.setName(movie.getName());
        movieReference.setPosterPath(movie.getPosterPath());
        return movieReference;
    }
    
    public static MovieDto convertMovieToDto(Movie movie) {
        MovieDto movieDto = new MovieDto();
        movieDto.setId(movie.getId());
        movieDto.setSynopsis(movie.getSynopsis());
        movieDto.setPosterPath(movie.getPosterPath());
        movieDto.setBackdropPath(movie.getBackdropPath());
        movieDto.setRuntime(movie.getRuntime());
        movieDto.setName(movie.getName());
        List<Genre> genres = movie.getGenres();
        List<String> genresNameList = new ArrayList<>();
        for(Genre genre : genres)
        	genresNameList.add(genre.getName());
        movieDto.setGenres(genresNameList);
        movieDto.setReleaseDate(movie.getReleaseDate());
        movieDto.setYear(movie.getYear());
        return movieDto;
    }
    
    /*
    public static Movie getMovieFromDto(MovieDto movieDto, List<Genre> genres) {
        Movie movie = new Movie();
        movie.setMediaID(movieDto.getMediaID());
        movie.setSynopsis(movieDto.getSynopsis());
        movie.setPosterPath(movieDto.getPosterPath());
        movie.setBackdropPath(movieDto.getBackdropPath());
        movie.setRuntime(movieDto.getRuntime());
        movie.setName(movieDto.getMediaName());
        movie.setGenres(genres);
        movie.setReleaseDate(movieDto.getReleaseDate());
        movie.setYear(movieDto.getYear());
        return movie;
    }*/
    
    private static void updateMovieByDto(Movie movie, MovieDto movieDto) {
    	String synopsis = movieDto.getSynopsis();
    	if(synopsis != null)
    		movie.setSynopsis(synopsis);
    	String posterPath = movieDto.getPosterPath();
    	if(posterPath != null)
    		movie.setPosterPath(posterPath);
    	String backdropPath = movieDto.getBackdropPath();
    	if(backdropPath != null)
    		movie.setBackdropPath(backdropPath);
        movie.setRuntime(movieDto.getRuntime());
    	String name = movieDto.getName();
    	if(name != null)
    		movie.setName(name);
        movie.setReleaseDate(movieDto.getReleaseDate());
        movie.setYear(movieDto.getYear());
    }
    
    public Movie getMovieByNameID(String mediaID) throws EntityNotFoundException {
    	return movieRepository.findByMediaID(mediaID).
    			orElseThrow(() -> new EntityNotFoundException("The Movie with id: ("+mediaID+") does not exists"));
    }
    
    public Movie getMovieByID(Long id) throws EntityNotFoundException {
    	return movieRepository.findById(id).
    			orElseThrow(() -> new EntityNotFoundException("The Movie with id: ("+id+") does not exists"));
    }
    
}
