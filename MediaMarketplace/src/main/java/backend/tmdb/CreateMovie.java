package backend.tmdb;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.DataUtils;
import backend.controllers.ActorController;
import backend.controllers.DirectorController;
import backend.controllers.GenreController;
import backend.controllers.MovieController;
import backend.controllers.PersonController;
import backend.dto.input.ActorReference;
import backend.dto.input.DirectorReference;
import backend.dto.mediaProduct.CreateMovieDto;
import backend.dto.mediaProduct.MovieDto;
import backend.dto.mediaProduct.MovieReference;
import backend.dto.mediaProduct.PersonDto;
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
	
	private static Random rnd = new Random();
	
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
		List<CreateMovieDto> createMovieDtos = new ArrayList<>();
		if(movieDbs != null) for(MovieDb movieDb : movieDbs) {
			CreateMovieDto createMovieDto = getCreateMovieDto(movieDb);
			MovieDto movieDto = createMovieDto.getMovieDto();
			movieDto.setPosterPath(TMDB_IMAGE_FULL_URL+"/w92"+movieDb.getPosterPath());
			createMovieDtos.add(createMovieDto);
		}
		movieResultsPage.getPage();
		MovieDtoSearchResult movieDtoSearchResult = new MovieDtoSearchResult();
		movieDtoSearchResult.setSearchText(text);
		movieDtoSearchResult.setResultList(createMovieDtos);
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
		
		
		CreateMovieDto createMovieDto = getCreateMovieDto(movieDb);
		MovieDto movieDto = createMovieDto.getMovieDto();
		createGenres(movieDto);
		String movieMediaID = createMovieDto.getMediaID();
		try {
			LOGGER.info("Starting to create the movie information");
			movieController.addMovie(createMovieDto);
		} catch (EntityNotFoundException e) {
			//this can happen if a genre does not exist, but we already added/verified that all of them are in the database, therefore the exception won't be triggered
		} catch (EntityAlreadyExistsException e) {
			throw new CanUpdateException(createMovieDto, e);
		}
		
		addMovieImages(movieDb, movieDto, exceptionList);
		addDirectors(movieDb.getCrew(), movieMediaID, exceptionList);
		addActors(movieDb.getCast(), movieMediaID, exceptionList);
		if(!exceptionList.isEmpty())
			throw new CreateMovieException(exceptionList, "There were exceptions with creating the movie");
	}
	
	public void addMovieToDatabase(CreateMovieDto createMovieDto) throws NumberFormatException, CreateMovieException, CanUpdateException {
		String mediaId = createMovieDto.getMediaID();
		addMovieToDatabase(Integer.parseInt(mediaId));
	}
	
	/*
	public void updateMovieInDatabase(Long movieId) throws EntityNotFoundException, NumberFormatException, CreateMovieException {
		String mediaId = movieController.getMovieMediaID(movieId);
		MovieDb movieDb = getMovieDb(Integer.parseInt(mediaId));
		updateMovieInDatabase(movieDb);
	}
	*/
	
	public void updateMovieInDatabase(CanUpdateException exception) throws CreateMovieException, EntityNotFoundException {
		updateMovieInDatabase(exception.getCreateMovieDto());
	}
	
	private void updateMovieInDatabase(CreateMovieDto createMovieDto) throws NumberFormatException, CreateMovieException, EntityNotFoundException {
		String mediaId = createMovieDto.getMediaID();
		updateMovieInDatabase(Integer.parseInt(mediaId));
	}
	
	public void updateMovieInDatabase(MovieReference movieReference) throws EntityNotFoundException, NumberFormatException, CreateMovieException {
		String mediaId = movieController.getMovieMediaID(movieReference.getId());
		updateMovieInDatabase(Integer.parseInt(mediaId));
	}
	
	private static final Logger LOGGER = Logger.getLogger(CreateMovie.class.getName());
	
    static {
        LOGGER.setUseParentHandlers(false);
    }
	
	public Logger getLogger() {
		return LOGGER;
	}
	
	/**
	 * 
	 * @param movieId
	 * @throws EntityAlreadyExistsException if this is triggered, then the entity is already created
	 * @throws CreateMovieException 
	 * @throws EntityNotFoundException 
	 */
	private void updateMovieInDatabase(int mediaId) throws CreateMovieException, EntityNotFoundException {
		List<NameAndException> exceptionList = new ArrayList<>();
		MovieDb movieDb = getMovieDb(mediaId);
		CreateMovieDto createMovieDto = getCreateMovieDto(movieDb);
		MovieDto movieDto = createMovieDto.getMovieDto();
		createGenres(movieDto);
		String movieMediaID = createMovieDto.getMediaID();
		LOGGER.info("Starting to update the movie information");
		final Long movieId = movieController.updateMovie(createMovieDto);
		movieDto.setId(movieId);
		addMovieImages(movieDb, movieDto, exceptionList);
		List<PersonCrew> crew = movieDb.getCrew();
		if(crew != null) {
			LOGGER.info("Removing all of the movie directors");
			try {
				directorController.removeAllDirectorsFromMovie(movieId);
			}
			catch (EntityNotFoundException e) {
				//this will not happen, because we checked that the movie is created.
			}
			LOGGER.info("Finished removing");
			addDirectors(crew, movieMediaID, exceptionList);
		}
		List<PersonCast> cast = movieDb.getCast();
		if(cast != null) {
			LOGGER.info("Removing all of the movie actors");
			try {
				actorController.removeAllActorsFromMovie(movieId);
			}
			catch (EntityNotFoundException e) {
				//this will not happen, because we checked that the movie is created.
			}
			LOGGER.info("Finished removing");
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
		LOGGER.info("Starting to create genres");
		List<String> genres = movieDto.getGenres();
		for(String genre : genres) {
			try {
				genreController.createGenre(genre);
				LOGGER.info(" The Genre \"" + genre + "\" has been created");
			}
			catch (EntityAlreadyExistsException e) {
				//this is okay, can be if the gene is already created, therefore we don't need to handle the exception
			}
		}
	}
	
	private void addMovieImages(MovieDb movieDb, MovieDto movieDto, List<NameAndException> exceptionList) {
		LOGGER.info("Adding poster to the movie");
		try {
			saveImageFromURL(movieDb.getPosterPath(), movieDto.getPosterPath());
		} catch (IOException e) {
			exceptionList.add(new NameAndException("Poster Creation Failed", e));
		}
		LOGGER.info("Adding backdrop to the movie");
		try {
			saveImageFromURL(movieDb.getBackdropPath(), movieDto.getBackdropPath());
		} catch (IOException e) {
			exceptionList.add(new NameAndException("Backdrop Creation Failed", e));
		}
	}
	
	private void addMovieImages(CreateMovieDto createMovieDto, MovieDto movieDto, List<NameAndException> exceptionList) {
		LOGGER.info("Adding poster to the movie");
		MovieDto inputMovieDto = createMovieDto.getMovieDto();
		try {
			saveImageFromURL(inputMovieDto.getPosterPath(), movieDto.getPosterPath());
		} catch (IOException e) {
			exceptionList.add(new NameAndException("Poster Creation Failed", e));
		}
		LOGGER.info("Adding backdrop to the movie");
		try {
			saveImageFromURL(inputMovieDto.getBackdropPath(), movieDto.getBackdropPath());
		} catch (IOException e) {
			exceptionList.add(new NameAndException("Backdrop Creation Failed", e));
		}
	}
	
	private void addDirectors(List<PersonCrew> crew, String movieMediaID, List<NameAndException> exceptionList) {
		if(crew != null) for(PersonCrew personCrew : crew) {
			String job = personCrew.getJob();
			if(DataUtils.equalsIgnoreCase(job, DIRECTOR)) {
				PersonDto personDto = getPersonDto(personCrew, exceptionList);
				DirectorReference directorDto = new DirectorReference();
				directorDto.setMovieMediaId(movieMediaID);
				directorDto.setPersonMediaID(personDto.getPersonMediaID());
				try {
					directorController.addDirector(directorDto);
					LOGGER.info("The Director \"" + personDto.getName() +"\" has been added");
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
			ActorReference actorDto = new ActorReference();
			actorDto.setRoleName(personCast.getCharacter());
			actorDto.setMovieMediaId(movieMediaID);
			actorDto.setPersonMediaID(personDto.getPersonMediaID());
			try {
				actorController.addActor(actorDto);
				LOGGER.info("The Actor \"" + personDto.getName() + "\" in the role \"" + actorDto.getRoleName() + "\" has been added");
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
	
	private CreateMovieDto getCreateMovieDto(MovieDb movieDb) {
		CreateMovieDto createMovieDto = new CreateMovieDto();
		MovieDto movieDto = getMovieDto(movieDb);
		String mediaId = ""+movieDb.getId();
		createMovieDto.setMediaID(mediaId);
		String posterPath = movieDb.getPosterPath();
		if(posterPath != null)
			movieDto.setPosterPath(POSTERS_PATH+mediaId+".jpg");
		String backdropPath = movieDb.getBackdropPath();
		if(backdropPath != null)
			movieDto.setBackdropPath(BACKDROP_PATH+mediaId+".jpg");
		createMovieDto.setMovieDto(movieDto);
		return createMovieDto;
	}
	
	private MovieDto getMovieDto(MovieDb movieDb) {
		MovieDto movieDto = new MovieDto();
		movieDto.setName(movieDb.getOriginalTitle());
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
				LOGGER.info("The Person \"" + personDto.getName() +"\" has been added");
				String personImage = person.getProfilePath();
				LOGGER.info("Saving Person \"" + personDto.getName() +"\" image");
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
		if(!file.exists()) {
			check(PEOPLE_PATH);check(POSTERS_PATH);check(BACKDROP_PATH);
			URL url;
			try {
				url = URI.create(TMDB_IMAGE_URL + inputURL).toURL();
			}
			catch (Exception e) {
				throw new IOException("Failed to write");
			}
			BufferedImage image = ImageIO.read(url);
			if(!ImageIO.write(image, "jpg", file))
				throw new IOException("Failed to write");
			sleep();
		}
		return file;
	}
	
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
