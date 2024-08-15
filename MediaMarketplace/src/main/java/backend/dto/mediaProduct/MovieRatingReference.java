package backend.dto.mediaProduct;

public class MovieRatingReference {
	
    private Long movieId;
    
    private Long userId;
    
	private Integer rating;

	public Long getMovieId() {
		return movieId;
	}

	public Long getUserId() {
		return userId;
	}

	public Integer getRating() {
		return rating;
	}

	public void setMovieId(Long movieId) {
		this.movieId = movieId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}	
}
