package backend.tmdb;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import Interface.helpAction;
import backend.DataUtils;
import backend.controllers.ActorController;
import backend.controllers.DirectorController;
import backend.controllers.GenreController;
import backend.controllers.MovieController;
import backend.controllers.PersonController;
import backend.dto.mediaProduct.ActorDto;
import backend.dto.mediaProduct.DirectorDto;
import backend.dto.mediaProduct.MovieDto;
import backend.dto.mediaProduct.PersonDto;
import backend.entities.Actor;
import backend.entities.Director;
import backend.entities.Movie;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbMovies.MovieMethod;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.model.people.Person;
import info.movito.themoviedbapi.model.people.PersonCast;
import info.movito.themoviedbapi.model.people.PersonCrew;

@Component
public class CreateMovie {
	
	public static final String TMDB_IMAGE_FULL_URL = "https://www.themoviedb.org/t/p";
	
	public static final String TMDB_IMAGE_URL = "https://image.tmdb.org/t/p/original";
	
	public static final String TMDB_API_KEY = "1ebdc434f5ee818a93a99347f02a76bf";
	
	public static final String DIRECTOR = "director";
	
	public static final String POSTERS_PATH = "posters/";
	public static final String BACKDROP_PATH = "backdrops/";
	public static final String PEOPLE_PATH = "people/";
	
	@Autowired
	private GenreController genreController;
	
	@Autowired
	private MovieController movieController;
	
	@Autowired
	private PersonController personController;
	
	@Autowired
	private ActorController actorController;
	
	@Autowired
	private DirectorController directorController;
	
	public MovieDtoSearchResult searchMovie(String text) {
		return searchMovie(text, null);
	}
	
	public MovieDtoSearchResult searchMovie(String text, Integer page) {
		TmdbApi tmdbApi = new TmdbApi(TMDB_API_KEY);
		TmdbSearch tmdbSearch = tmdbApi.getSearch();
		MovieResultsPage movieResultsPage = tmdbSearch.searchMovie(text, null, "en-US", false, page);
		List<MovieDb> movieDbs = movieResultsPage.getResults();
		List<MovieDto> movieDtos = new ArrayList<>();
		if(movieDbs != null) for(MovieDb movieDb : movieDbs) {
			MovieDto movieDto = getMovieDto(movieDb);
			movieDto.setPosterPath(TMDB_IMAGE_FULL_URL+"/w92"+movieDb.getPosterPath());
			movieDtos.add(movieDto);
		}
		movieResultsPage.getPage();
		MovieDtoSearchResult movieDtoSearchResult = new MovieDtoSearchResult();
		movieDtoSearchResult.setSearchText(text);
		movieDtoSearchResult.setResultList(movieDtos);
		movieDtoSearchResult.setCurrentPage(movieResultsPage.getPage());
		movieDtoSearchResult.setTotalPages(movieResultsPage.getTotalPages());
		return movieDtoSearchResult;
	}
	
	/**
	 * 
	 * @param movieId
	 * @throws EntityAlreadyExistsException if this is triggered, then the entity is already created
	 * @throws CreateMovieException 
	 * @throws CanUpdateException if movie exists, let the user have an option to update the movie
	 */
	public void addMovieToDatabase(int movieId) throws CreateMovieException, CanUpdateException {
		List<NameAndException> exceptionList = new ArrayList<>();
		MovieDb movieDb = getMovieDb(movieId);
		
		MovieDto movieDto = getMovieDto(movieDb);
		createGenres(movieDto);
		String movieMediaID = movieDto.getMediaID();
		try {
			movieController.addMovie(movieDto);
		} catch (EntityNotFoundException e) {
			//this can happen if a genre does not exist, but we already added/verified that all of them are in the database, therefore the exception won't be triggered
		} catch (EntityAlreadyExistsException e) {
			throw new CanUpdateException(movieDb, e);
		}
		
		addMovieImages(movieDb, movieDto, exceptionList);
		addDirectors(movieDb.getCrew(), movieMediaID, exceptionList);
		addActors(movieDb.getCast(), movieMediaID, exceptionList);
		if(!exceptionList.isEmpty())
			throw new CreateMovieException(exceptionList, "There were exceptions with creating the movie");
	}
	
	public void updateMovieInDatabase(int movieId) throws CreateMovieException, EntityNotFoundException {
		MovieDb movieDb = getMovieDb(movieId);
		updateMovieInDatabase(movieDb);
	}
	
	public void updateMovieInDatabase(CanUpdateException exception) throws CreateMovieException, EntityNotFoundException {
		updateMovieInDatabase(exception.getMovieDb());
	}
	
	/**
	 * 
	 * @param movieId
	 * @throws EntityAlreadyExistsException if this is triggered, then the entity is already created
	 * @throws CreateMovieException 
	 * @throws EntityNotFoundException 
	 */
	private void updateMovieInDatabase(MovieDb movieDb) throws CreateMovieException, EntityNotFoundException {
		List<NameAndException> exceptionList = new ArrayList<>();
		MovieDto movieDto = getMovieDto(movieDb);
		createGenres(movieDto);
		String movieMediaID = movieDto.getMediaID();
		movieController.updateMovie(movieDto);
		
		addMovieImages(movieDb, movieDto, exceptionList);
		List<PersonCrew> crew = movieDb.getCrew();
		if(crew != null) {
			try {
				directorController.removeAllDirectorsFromMovie(movieDto);
			}
			catch (EntityNotFoundException e) {
				//this will not happen, because we checked that the movie is created.
			}
			addDirectors(crew, movieMediaID, exceptionList);
		}
		List<PersonCast> cast = movieDb.getCast();
		if(cast != null) {
			try {
				actorController.removeAllActorsFromMovie(movieDto);
			}
			catch (EntityNotFoundException e) {
				//this will not happen, because we checked that the movie is created.
			}
			addActors(cast, movieMediaID, exceptionList);
		}
		if(!exceptionList.isEmpty())
			throw new CreateMovieException(exceptionList, "There were exceptions with updating the movie");
	}
	
	private MovieDb getMovieDb(int movieId) {
		TmdbApi tmdbApi = new TmdbApi(TMDB_API_KEY);
		TmdbMovies tmdbMovies = tmdbApi.getMovies();
		return tmdbMovies.getMovie(movieId, "en-US", MovieMethod.credits);
	}
	
	private void createGenres(MovieDto movieDto) {
		List<String> genres = movieDto.getGenres();
		for(String genre : genres) {
			try {
				genreController.createGenre(genre);
			}
			catch (EntityAlreadyExistsException e) {
				//this is okay, can be if the gene is already created, therefore we don't need to handle the exception
			}
		}
	}
	
	private void addMovieImages(MovieDb movieDb, MovieDto movieDto, List<NameAndException> exceptionList) {
		try {
			saveImageFromURL(movieDb.getPosterPath(), movieDto.getPosterPath());
		} catch (IOException e) {
			exceptionList.add(new NameAndException("Poster Creation Failed", e));
		}
		try {
			saveImageFromURL(movieDb.getBackdropPath(), movieDto.getBackdropPath());
		} catch (IOException e) {
			exceptionList.add(new NameAndException("Backdrop Creation Failed", e));
		}
	}
	
	private void addDirectors(List<PersonCrew> crew, String movieMediaID, List<NameAndException> exceptionList) {
		if(crew != null) for(PersonCrew personCrew : crew) {
			String job = personCrew.getJob();
			if(DataUtils.equalsIgnoreCase(job, DIRECTOR)) {
				PersonDto personDto = getPersonDto(personCrew, exceptionList);
				DirectorDto directorDto = new DirectorDto();
				directorDto.setMovieMediaId(movieMediaID);
				directorDto.setPersonMediaID(personDto.getPersonMediaID());
				try {
					directorController.addDirector(directorDto);
				} catch (EntityNotFoundException e) {
					//we checked that the movie and person exists
					//but if there is a problem we will handle it
					exceptionList.add(new NameAndException("Director Creation Failed", e));
				} catch (EntityAlreadyExistsException e) {
					//this is okay, can be if the person is already a director of the movie, therefore we don't need to handle the exception
				}
			}
		}
	}
	
	private void addActors(List<PersonCast> cast, String movieMediaID, List<NameAndException> exceptionList) {
		final int max = 15;
		int count = 0;
		if(cast != null) for(PersonCast personCast : cast) {
			PersonDto personDto = getPersonDto(personCast, exceptionList);
			ActorDto actorDto = new ActorDto();
			actorDto.setRoleName(personCast.getCharacter());
			actorDto.setMovieMediaId(movieMediaID);
			actorDto.setPersonMediaID(personDto.getPersonMediaID());
			try {
				actorController.addActor(actorDto);
			} catch (EntityNotFoundException e) {
				//we checked that the movie and person exists
				//but if there is a problem we will handle it
				exceptionList.add(new NameAndException("Director Creation Failed", e));
			} catch (EntityAlreadyExistsException e) {
				//this is okay, can be if the person is already an actor of the movie, therefore we don't need to handle the exception
			}
			count++;
			if(count >= max)
				break;
		}
	}
	
	private MovieDto getMovieDto(MovieDb movieDb) {
		MovieDto movieDto = new MovieDto();
		movieDto.setMediaName(movieDb.getOriginalTitle());
		movieDto.setRuntime(movieDb.getRuntime());
		movieDto.setSynopsis(movieDb.getOverview());
		List<Genre> genres = movieDb.getGenres();
		List<String> movieGenres = new ArrayList<>();
		if(genres != null) for(Genre genre : genres)
			movieGenres.add(genre.getName());
		movieDto.setGenres(movieGenres);
		String releaseDateStr = movieDb.getReleaseDate();
		try {
			LocalDate releaseDate = LocalDate.parse(releaseDateStr);
			movieDto.setReleaseDate(releaseDate);
			movieDto.setYear(releaseDate.getYear());
		}
		catch (DateTimeParseException e) {
			//if this happens, then don't set the release date of the movie
		}
		movieDto.setMediaID(""+movieDb.getId());
		String posterPath = movieDb.getPosterPath();
		if(posterPath != null)
			movieDto.setPosterPath(POSTERS_PATH+movieDto.getMediaID()+".jpg");
		String backdropPath = movieDb.getBackdropPath();
		if(backdropPath != null)
			movieDto.setBackdropPath(BACKDROP_PATH+movieDto.getMediaID()+".jpg");
		return movieDto;
	}
	
	private PersonDto getPersonDto(Person person, List<NameAndException> exceptionList) {
		PersonDto personDto = new PersonDto();
		personDto.setName(person.getName());
		personDto.setPersonMediaID(""+person.getId());
		personDto.setImagePath(PEOPLE_PATH+personDto.getPersonMediaID()+".jpg");
		try {
			try {
				personController.addPerson(personDto);
				String personImage = person.getProfilePath();
				saveImageFromURL(personImage, personDto.getImagePath());
			} catch (IOException e) {
				exceptionList.add(new NameAndException("Person Image Creation Failed", e));
			}
		}
		catch (EntityAlreadyExistsException e) {
			//this can happen when the person is already in the database, therefore we don't need to handle this exception
		}
		return personDto;
	}
	
	public static File saveImageFromURL(String inputURL, String destPath) throws IOException {
		File file = new File(destPath);
		System.out.println("File: " + file);
		if(!file.exists()) {
			check(PEOPLE_PATH);check(POSTERS_PATH);check(BACKDROP_PATH);
			URL url = new URL(TMDB_IMAGE_URL + inputURL);
			BufferedImage image = ImageIO.read(url);
			if(!ImageIO.write(image, "jpg", file))
				throw new IOException("Failed to write");
			sleep();
		}
		return file;
	}
	
	private static Random rnd = new Random();
	
	private static void sleep() {
		int num = rnd.nextInt(1500)+1000;
		try {
			Thread.sleep(num);
		}
		catch(InterruptedException exp) {
			Thread.currentThread().interrupt();
		}
	}
	
	private static void check(String path) {
		File file = new File(path);
		if(!file.exists())
			file.mkdir();
	}

}
