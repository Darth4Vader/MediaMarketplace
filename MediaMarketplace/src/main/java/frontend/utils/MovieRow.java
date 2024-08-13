package frontend.utils;

import java.util.ArrayList;
import java.util.List;

import backend.dto.mediaProduct.MovieReference;

public class MovieRow {
	
	private List<MovieReference> movies;
	
	public MovieRow() {
		this.movies = new ArrayList<>();
	}

	public MovieRow(List<MovieReference> movies) {
		super();
		this.movies = movies;
	}
	
	public void add(MovieReference movie) {
		this.movies.add(movie);
	}

	public List<MovieReference> getMovies() {
		return movies;
	}

	public void setMovies(List<MovieReference> movies) {
		this.movies = movies;
	}
	
}