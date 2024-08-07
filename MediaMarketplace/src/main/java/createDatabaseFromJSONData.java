import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import Interface.helpAction;
import MediaData.Actor;
import MediaData.MediaSimple;
import MediaData.MediaUtils;
import MediaData.Movie;
import MediaData.CreateData.TmdbGoogleScrapper;
import backend.ActivateSpringApplication;
import backend.controllers.ActorController;
import backend.controllers.GenreController;
import backend.controllers.MovieController;
import backend.controllers.PersonController;
import backend.controllers.ProductController;
import backend.dto.mediaProduct.ActorDto;
import backend.dto.mediaProduct.MovieDto;
import backend.dto.mediaProduct.PersonDto;
import backend.dto.mediaProduct.ProductDto;
import backend.entities.Director;
import backend.entities.Genre;
import backend.entities.Person;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.ActorRepository;
import backend.repositories.DirectorRepository;
import backend.repositories.GenreRepository;
import backend.repositories.MovieRepository;
import backend.repositories.PersonRepository;
import backend.services.MovieService;
import backend.tmdb.CreateMovie;

public class createDatabaseFromJSONData {
	
	private static ConfigurableApplicationContext context;
	
	public static void main(String... args) throws BeansException, Exception {
		
		context = ActivateSpringApplication.create(args);
		
		
		
		//PersonController genreRep = context.getBean(PersonController.class);
		
		MovieController genreRep = context.getBean(MovieController.class);
		for(backend.entities.Movie movie : genreRep.getAllMovies()) {
			List<Director> directors = movie.getDirectors();
			System.out.println(directors + " " + movie.getName());
			if(directors != null && directors.size() > 0) {
				Director director = directors.get(0);
			}
		}
		
		/*for(Person person : genreRep.getAllPeople()) {
			genreRep.removePerson(person.getId());
		}
		
		CreateMovie createMovie = context.getBean(CreateMovie.class);
		
		createMovie.addMovieToDatabase(122);
		*/
		
		/*addPeople();
		addGenres();
		addMovies();
		addProducts();
		validateMovies();*/
		
		/*addPeople();
		addMovies();*/
		
		/*MovieController genreRep2 = context.getBean(MovieController.class);
		MovieRepository movieRepository = context.getBean(MovieRepository.class);
		for(backend.entities.Movie movie : genreRep2.getAllMovies()) {
			List<Movie> list = MediaUtils.getAll(Movie.class);
			for(Movie moviel : list) {
				if(moviel.getTmdbID().equals(movie.getMediaID())) {
					movie.setPosterPath("posters/"+moviel.getImdbID()+".jpg");
					movieRepository.save(movie);
					break;
				}
			}
			System.out.println(movie.getActorsRoles());
		}*/
		
		/*MovieController genreRep2 = context.getBean(MovieController.class);
		ProductController productController = context.getBean(ProductController.class);
		for(backend.entities.Movie movie : genreRep2.getAllMovies()) {
			System.out.println(movie.getName());
			if(movie.getName().startsWith("The Hobbit")) {
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
		}*/
		
		/*ActorRepository genreRep = context.getBean(ActorRepository.class);
		genreRep.deleteAll();
		DirectorRepository genreRep2 = context.getBean(DirectorRepository.class);
		genreRep2.deleteAll();
		PersonRepository genreRep3 = context.getBean(PersonRepository.class);
		genreRep3.deleteAll();
		/*MovieRepository genreRep4 = context.getBean(MovieRepository.class);
		genreRep4.deleteAll();
		MovieRepository genreRep5 = context.getBean(MovieRepository.class);
		genreRep5.deleteAll();
		/*addPeople();
		addGenres();
		addMovies();
		addProducts();
		validateMovies();*/
		
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
	
	public static void addPeople() throws IOException {
		PersonController personController = context.getBean(PersonController.class);
		//MediaGenreRepository genreRep = context.getBean(MediaGenreRepository.class);
		List<Movie> list = MediaUtils.getAll(Movie.class);
		for(Movie movie : list) {
			List<Actor> actors = movie.getMainCast();
			if(actors != null) for(Actor actor : actors) {
				try {
					PersonDto personDto = new PersonDto();
					personDto.setName(actor.getName());
					personDto.setPersonMediaID(actor.getImdbID());
					personDto.setImagePath("actors/"+actor.getImdbID()+".jpg");
					personController.addPerson(personDto);
				}
				catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}
	}
	
	public static void addMovies() throws IOException {
		MovieController movieController = context.getBean(MovieController.class);
		ActorController actorController = context.getBean(ActorController.class);
		List<Movie> list = MediaUtils.getAll(Movie.class);
		for(Movie movie : list) {
	        MovieDto mediaDto = new MovieDto();
	        mediaDto.setMediaID(movie.getTmdbID());
	        mediaDto.setSynopsis(movie.getSynopsis());
	        mediaDto.setPosterPath("posters/"+movie.getImdbID()+".jpg");
	        //mediaDto.setPrice(mediaDto.getPrice());
	        mediaDto.setMediaName(movie.getName());
	        mediaDto.setGenres(movie.getGenres());
	        
	        List<Actor> actors = movie.getMainCast();
	        List<ActorDto> actorsList = new ArrayList<>();
	        //mediaDto.setActors(actorsList);
	        //mediaDto.setPrice("10");
	        try {
	        	System.out.println(movie.getName());
	        	System.out.println(mediaDto.getMediaID());
	        	System.out.println(movie.getGenres());
				movieController.addMovie(mediaDto);
			} catch (EntityAlreadyExistsException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        try {
		        if(actors != null) for(Actor actor : actors) {
		        	ActorDto actorDto = new ActorDto();
		        	actorDto.setRoleName(actor.getRole());
		        	actorDto.setPersonMediaID(actor.getImdbID());
		        	actorDto.setMovieMediaId(movie.getTmdbID());
		        	actorController.addActor(actorDto);
		        }
	        } catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (EntityAlreadyExistsException e) {
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
