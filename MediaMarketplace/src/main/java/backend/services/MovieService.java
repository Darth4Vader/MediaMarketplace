package backend.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import backend.dto.mediaProduct.MovieDto;
import backend.entities.Genre;
import backend.entities.Movie;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.MovieRepository;

@Service
public class MovieService {
	
    @Autowired
    private MovieRepository movieRepository;
    
    @Autowired
    private GenreService genreService;
    
    public List<Movie> getAllMovies() {
    	return movieRepository.findAll();
    }
	
    public void addMovie(MovieDto movieDto) throws EntityAlreadyExistsException, EntityNotFoundException {
    	String mediaID = movieDto.getMediaID();
    	try {	
    		getMovieByNameID(mediaID);
    		throw new EntityAlreadyExistsException("The Movie with mediaId: ("+mediaID+") does already exists");
    	}
    	catch (EntityNotFoundException e) {} // this is what we expect to get, that's why we don't throw
		List<String> genresNames = movieDto.getGenres();
		List<Genre> genres = new ArrayList<>();
		for(String genreName : genresNames)
			genres.add(genreService.getGenreByName(genreName));
		Movie mediaProduct = getMovieFromDto(movieDto, genres);
    	movieRepository.save(mediaProduct);
    }
    
    public static Movie getMovieFromDto(MovieDto movieDto, List<Genre> genres) {
        Movie movie = new Movie();
        movie.setMediaID(movieDto.getMediaID());
        movie.setSynopsis(movieDto.getSynopsis());
        movie.setImagePath(movieDto.getImagePath());
        movie.setName(movieDto.getMediaName());
        movie.setGenres(genres);
        return movie;
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
