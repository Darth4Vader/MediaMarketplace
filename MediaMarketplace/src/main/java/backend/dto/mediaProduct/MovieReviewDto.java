package backend.dto.mediaProduct;

public class MovieReviewDto {
	
	private MovieReviewReference movieReview;
	private String userName;
	
	public MovieReviewReference getMovieReview() {
		return movieReview;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setMovieReview(MovieReviewReference movieReview) {
		this.movieReview = movieReview;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
}
