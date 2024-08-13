package backend.dto.mediaProduct;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;

import com.fasterxml.jackson.annotation.JsonFormat;

import backend.entities.Movie;
import backend.entities.User;
import io.micrometer.common.lang.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class MovieReviewReference {
	
    private Long movieId;
    
    private Long userId;
    
	private Integer rating;
	
	private String reviewTitle;
	
	private String review;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private LocalDateTime createdDate;

	public MovieReviewReference() {
	}

	public Long getMovieId() {
		return movieId;
	}

	public Long getUserId() {
		return userId;
	}

	public Integer getRating() {
		return rating;
	}

	public String getReviewTitle() {
		return reviewTitle;
	}

	public String getReview() {
		return review;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
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

	public void setReviewTitle(String reviewTitle) {
		this.reviewTitle = reviewTitle;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

}
