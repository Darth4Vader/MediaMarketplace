import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import Interface.helpAction;
import MediaData.CreateMovie;
import MediaData.MediaSimple;
import MediaData.MediaUtils;
import MediaData.Movie;
import MediaData.CreateData.TmdbGoogleScrapper;
import backend.ActivateSpringApplication;
import backend.controllers.GenreController;
import backend.controllers.MovieController;
import backend.controllers.ProductController;
import backend.dto.mediaProduct.MovieDto;
import backend.dto.mediaProduct.ProductDto;
import backend.entities.Genre;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.GenreRepository;
import backend.repositories.MovieRepository;
import backend.services.MovieService;

public class createDatabaseFromJSONData {
	
	private static ConfigurableApplicationContext context;
	
	public static void main(String... args) throws BeansException, Exception {
		
		context = ActivateSpringApplication.create(args);
		addGenres();
		addMovies();
		addProducts();
		validateMovies();
		
		//validateMovies0222();
	}
	
	public static void addGenres() throws IOException {
		GenreController genreRep = context.getBean(GenreController.class);
		System.out.println(genreRep.getAllGenres());
		
		
		
		//MediaGenreRepository genreRep = context.getBean(MediaGenreRepository.class);
		List<Movie> list = MediaUtils.getAll(Movie.class);
		for(Movie movie : list) {
			List<String> genres = movie.getGenres();
			if(genres != null) for(String genre : genres) {
				try {
					genreRep.createGenre(genre);
				}
				catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}
	}
	
	public static void addMovies() throws IOException {
		MovieController movieController = context.getBean(MovieController.class);
		List<Movie> list = MediaUtils.getAll(Movie.class);
		for(Movie movie : list) {
	        MovieDto mediaDto = new MovieDto();
	        mediaDto.setMediaID(movie.getTmdbID());
	        mediaDto.setSynopsis(movie.getSynopsis());
	        mediaDto.setImagePath("posters/"+movie.getImdbID()+".jpg");
	        //mediaDto.setPrice(mediaDto.getPrice());
	        mediaDto.setMediaName(movie.getName());
	        mediaDto.setGenres(movie.getGenres());
	        //mediaDto.setPrice("10");
	        try {
	        	System.out.println(movie.getName());
	        	System.out.println(mediaDto.getMediaID());
	        	System.out.println(movie.getGenres());
				movieController.addMediaProduct(mediaDto);
			} catch (EntityAlreadyExistsException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void addProducts() throws IOException {
		MovieController movieController = context.getBean(MovieController.class);
		ProductController productController = context.getBean(ProductController.class);
		for(backend.entities.Movie movie : movieController.getAllMovies()) {
	        ProductDto productDto = new ProductDto();
	        productDto.setBuyPrice(0);
	        productDto.setRentPrice(0);
	        productDto.setMovieId(movie.getId());
	        try {
				productController.addProduct(productDto);
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";
	
	public static void validateMovies() throws IOException, EntityNotFoundException {
		MovieService mediaCont = context.getBean(MovieService.class);
		List<Movie> list = MediaUtils.getAll(Movie.class);
		for(Movie movie : list) {
			try {
				mediaCont.getMovieByNameID(movie.getTmdbID());
			}
			catch (Exception e) {
				System.err.println("Error for the media: " + movie);
				throw e;
			}
		}
		System.out.println(ANSI_GREEN + "All the movies are in the database" + ANSI_RESET);
	}
	
	public static void validateMovies0222() throws IOException, EntityNotFoundException {
		MovieRepository mediaCont = context.getBean(MovieRepository.class);
		mediaCont.deleteAll();
	}
}
