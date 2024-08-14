package backend.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.auth.AuthenticateAdmin;
import backend.entities.Genre;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.EntityRemovalException;
import backend.repositories.GenreRepository;

@Service
public class GenreService {
	
    @Autowired
    private GenreRepository genreRepository;
    
    //a non log user can get this information
    public List<String> getAllGenres() {
    	List<Genre> genres = genreRepository.findAll();
    	return covertGenresToDto(genres);
    }
	
    //only an admin can add a genre to the database
    @AuthenticateAdmin
    @Transactional
    public void createGenre(String genreName) throws EntityAlreadyExistsException {
    	try {	
    		getGenreByName(genreName);
    		throw new EntityAlreadyExistsException("The Genre with id: \""+genreName+"\" does already exists");
    	}
    	catch (EntityNotFoundException e) {}
    	Genre genre = new Genre(genreName);
    	genreRepository.save(genre);
    }
    
    //only an admin can remove a genre in the database
    @AuthenticateAdmin
    @Transactional
    public void removeGenre(String genreName) throws EntityNotFoundException, EntityRemovalException {
    	Genre genre = getGenreByName(genreName);
    	try {
    		genreRepository.delete(genre);
    	}
    	catch (Throwable e) {
    		throw new EntityRemovalException("Cannot Remove the Genre with id: \""+genreName+"\"");
		}
    }
    
    //only an admin can remove a genre in the database
    @AuthenticateAdmin
    public void removeGenreWithoutTransactional(String genreName) throws EntityNotFoundException, EntityRemovalException {
    	Genre genre = getGenreByName(genreName);
    	try {
    		genreRepository.delete(genre);
    	}
    	catch (Throwable e) {
    		throw new EntityRemovalException("Cannot Remove the Genre with id: \""+genreName+"\"");
		}
    }
    
    public static List<String> covertGenresToDto(List<Genre> genres) {
        List<String> genresNameList = new ArrayList<>();
        if(genres != null) for(Genre genre : genres)
        	genresNameList.add(genre.getName());
        return genresNameList;
    }
    
    public Genre getGenreByName(String genreName) throws EntityNotFoundException {
    	return genreRepository.findByName(genreName).
    			orElseThrow(() -> new EntityNotFoundException("The Genre with name: \""+genreName+"\" does not exists"));
    }
    
}
