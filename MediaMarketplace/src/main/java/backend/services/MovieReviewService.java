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

import backend.dto.mediaProduct.ProductDto;
import backend.entities.Genre;
import backend.entities.Movie;
import backend.entities.MovieReview;
import backend.entities.Product;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.MovieRepository;
import backend.repositories.MovieReviewRepository;
import backend.repositories.ProductRepository;

@Service
public class MovieReviewService {
	
    @Autowired
    private MovieReviewRepository movieReviewRepository;
    
    @Autowired
    private MovieService movieService;
    
    public List<MovieReview> getAllReviewOfMovie(Long movieId) throws EntityNotFoundException {
    	Movie movie = movieService.getMovieByID(movieId);
    	return movieReviewRepository.findAllByMovie(movie)
    			.orElseThrow(() -> new EntityNotFoundException("There are no reviews of the given movie"));
    }
    
}
