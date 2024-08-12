package frontend.utils;

import java.util.ArrayList;
import java.util.List;

import backend.entities.Movie;

public class MovieRow {
	
	private List<Movie> movies;
	
	public MovieRow() {
		this.movies = new ArrayList<>();
	}

	public MovieRow(List<Movie> movies) {
		super();
		this.movies = movies;
	}
	
	public void add(Movie movie) {
		this.movies.add(movie);
	}

	public List<Movie> getMovies() {
		return movies;
	}

	public void setMovies(List<Movie> movies) {
		this.movies = movies;
	}
	
}