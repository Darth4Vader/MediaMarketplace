import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/*import Interface.helpAction;
import MediaData.CreateMovie;
import MediaData.MediaSimple;
import MediaData.MediaUtils;
import MediaData.Movie;
import MediaData.CreateData.TmdbGoogleScrapper;*/
import backend.ActivateSpringApplication;
import backend.controllers.MediaGenreController;
import backend.controllers.MediaProductController;
import backend.dto.mediaProduct.MediaProductDto;
import backend.entities.Genre;
import backend.entities.Movie;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.MediaGenreRepository;
import backend.repositories.MovieRepository;
import backend.services.MovieService;

public class createDatabaseFromJSONData {
	
	private static ConfigurableApplicationContext context;
	
	public static void main(String... args) throws BeansException, Exception {
		
		//context = ActivateSpringApplication.create(args);
		//addGenres();
		//addMovies();
		//validateMovies();
	}
	
	/*public static void addGenres() throws IOException {
		MediaGenreController genreRep = context.getBean(MediaGenreController.class);
		System.out.println(genreRep.getAllGenres());
		
		
		
		//MediaGenreRepository genreRep = context.getBean(MediaGenreRepository.class);
		List<Movie> list = MediaUtils.getAll(Movie.class);
		for(Movie movie : list) {
			List<String> genres = movie.getGenres();
			if(genres != null) for(String genre : genres) {
				try {
					genreRep.createGenre(new MediaGenre(genre));
				}
				catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}
	}
	
	public static void addMovies() throws IOException {
		MediaProductController mediaCont = context.getBean(MediaProductController.class);
		List<Movie> list = MediaUtils.getAll(Movie.class);
		
		System.out.println(mediaCont.getAllMediaProducts());
		
		for(Movie movie : list) {
	        MediaProductDto mediaDto = new MediaProductDto();
	        mediaDto.setMediaID(movie.getTmdbID());
	        mediaDto.setSynopsis(movie.getSynopsis());
	        mediaDto.setImagePath("posters/"+movie.getImdbID()+".jpg");
	        //mediaDto.setPrice(mediaDto.getPrice());
	        mediaDto.setMediaName(movie.getName());
	        mediaDto.setGenres(movie.getGenres());
	        mediaDto.setPrice("10");
	        try {
	        	System.out.println(movie.getName());
	        	System.out.println(mediaDto.getMediaID());
	        	System.out.println(movie.getGenres());
				mediaCont.addMediaProduct(mediaDto);
			} catch (EntityAlreadyExistsException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
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
		MediaProductService mediaCont = context.getBean(MediaProductService.class);
		List<Movie> list = MediaUtils.getAll(Movie.class);
		for(Movie movie : list) {
			try {
				MediaProduct product = mediaCont.getMediaByID(movie.getTmdbID());
			}
			catch (Exception e) {
				System.err.println("Error for the media: " + movie);
				throw e;
			}
		}
		System.out.println(ANSI_GREEN + "All the movies are in the database" + ANSI_RESET);
	}
	*/
}
