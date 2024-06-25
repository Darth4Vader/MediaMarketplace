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
import java.util.List;
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
	
	public static final String TMDB_IMAGE_URL = "https://www.themoviedb.org/t/p/original";
	
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
	
	public MovieDtoSearchResult searchMovie(String text) throws ParseException {
		return searchMovie(text, null);
	}
	
	public MovieDtoSearchResult searchMovie(String text, Integer page) throws ParseException {
		TmdbApi tmdbApi = new TmdbApi(TMDB_API_KEY);
		TmdbSearch tmdbSearch = tmdbApi.getSearch();
		MovieResultsPage movieResultsPage = tmdbSearch.searchMovie(text, null, "en-US", false, page);
		List<MovieDb> movieDbs = movieResultsPage.getResults();
		List<MovieDto> movieDtos = new ArrayList<>();
		if(movieDbs != null) for(MovieDb movieDb : movieDbs) {
			System.out.println(movieDb);
			System.out.println(movieDb.getBackdropPath());
			System.out.println(movieDb.getPosterPath());
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
	
	public static class MovieDtoSearchResult {
		private String searchText;
		private List<MovieDto> resultList;
		private int currentPage;
		private int totalPages;
		public List<MovieDto> getResultList() {
			return resultList;
		}
		public int getCurrentPage() {
			return currentPage;
		}
		public int getTotalPages() {
			return totalPages;
		}
		public void setResultList(List<MovieDto> resultList) {
			this.resultList = resultList;
		}
		public void setCurrentPage(int currentPage) {
			this.currentPage = currentPage;
		}
		public void setTotalPages(int totalPages) {
			this.totalPages = totalPages;
		}
		public String getSearchText() {
			return searchText;
		}
		public void setSearchText(String searchText) {
			this.searchText = searchText;
		}
		
		
	}

	public void addMovieToDatabase(int movieId) throws ParseException {
		MovieDto movieDto = new MovieDto();
		String key = null;
		String movieID;
		TmdbApi tmdbApi = new TmdbApi(TMDB_API_KEY);
		TmdbMovies tmdbMovies = tmdbApi.getMovies();
		
		MovieDb movieDb = tmdbMovies.getMovie(movieId, "en-US", MovieMethod.credits);
		movieDto.setMediaName(movieDb.getOriginalTitle());
		movieDto.setRuntime(movieDb.getRuntime());
		movieDto.setSynopsis(movieDb.getOverview());
		List<Genre> genres = movieDb.getGenres();
		List<String> movieGenres = new ArrayList<>();
		for(Genre genre : genres) {
			String genreName = genre.getName();
			try {
				genreController.createGenre(genreName);
			}
			catch (EntityAlreadyExistsException e) {
				System.err.println(e.getMessage());
			}
			movieGenres.add(genreName);
		}
		movieDto.setGenres(movieGenres);
		String releaseDateStr = movieDb.getReleaseDate();
		LocalDate releaseDate = LocalDate.parse(releaseDateStr);
		movieDto.setReleaseDate(releaseDate);
		movieDto.setYear(releaseDate.getYear());
		String movieMediaID = ""+movieDb.getId();
		movieDto.setMediaID(movieMediaID);
		movieDto.setPosterPath(POSTERS_PATH+movieDto.getMediaID()+".jpg");
		movieDto.setBackdropPath(BACKDROP_PATH+movieDto.getMediaID()+".jpg");
		try {
			movieController.addMovie(movieDto);
		} catch (EntityAlreadyExistsException | EntityNotFoundException e) {
			System.err.println(e.getMessage());
		}
		
		try {
			saveImageFromURL(movieDb.getPosterPath(), movieDto.getPosterPath());
			saveImageFromURL(movieDb.getBackdropPath(), movieDto.getBackdropPath());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<PersonCrew> crew = movieDb.getCrew();
		for(PersonCrew personCrew : crew) {
			String job = personCrew.getJob();
			if(DataUtils.equalsIgnoreCase(job, DIRECTOR)) {
				PersonDto personDto = getPersonDto(personCrew);
				DirectorDto directorDto = new DirectorDto();
				directorDto.setMovieMediaId(movieMediaID);
				directorDto.setPersonMediaID(personDto.getPersonMediaID());
				try {
					directorController.addDirector(directorDto);
				} catch (EntityNotFoundException | EntityAlreadyExistsException e) {
					System.err.println(e.getMessage());
				}
			}
		}
		List<PersonCast> cast = movieDb.getCast();
		final int max = 15;
		int count = 0;
		for(PersonCast personCast : cast) {
			PersonDto personDto = getPersonDto(personCast);
			ActorDto actorDto = new ActorDto();
			actorDto.setRoleName(personCast.getCharacter());
			actorDto.setMovieMediaId(movieMediaID);
			actorDto.setPersonMediaID(personDto.getPersonMediaID());
			try {
				actorController.addActor(actorDto);
			} catch (EntityNotFoundException | EntityAlreadyExistsException e) {
				System.err.println(e.getMessage());
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
			// TODO: handle exception
			e.printStackTrace();
		}
		String movieMediaID = ""+movieDb.getId();
		movieDto.setMediaID(movieMediaID);
		
		movieDto.setPosterPath(TMDB_IMAGE_URL+movieDb.getPosterPath());
		movieDto.setBackdropPath(TMDB_IMAGE_URL+movieDb.getBackdropPath());
		
		//movieDto.setPosterPath(POSTERS_PATH+movieDto.getMediaID()+".jpg");
		//movieDto.setBackdropPath(BACKDROP_PATH+movieDto.getMediaID()+".jpg");
		return movieDto;
	}
	
	private PersonDto getPersonDto(Person person) {
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		catch (EntityAlreadyExistsException e) {
			System.err.println(e.getMessage());
		}
		return personDto;
	}
	
	public static File saveImageFromURL(String inputURL, String destPath) throws IOException {
		File file = new File(destPath);
		if(!file.exists()) {
			check(PEOPLE_PATH);check(POSTERS_PATH);check(BACKDROP_PATH);
			URL url = new URL(TMDB_IMAGE_URL + inputURL);
			BufferedImage image = ImageIO.read(url);
			if(!ImageIO.write(image, "jpg", file))
				throw new IllegalArgumentException("Failed to write");
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
