package backend.dto.mediaProduct;

public class CreateMovieDto {
	
	private String tmdbID;
	
	private MovieDto movieDto;

	public String getMediaID() {
		return tmdbID;
	}

	public void setMediaID(String tmdbID) {
		this.tmdbID = tmdbID;
	}

	public MovieDto getMovieDto() {
		return movieDto;
	}

	public void setMovieDto(MovieDto movieDto) {
		this.movieDto = movieDto;
	}
	
}
