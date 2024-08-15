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

public class MovieReviewReference extends MovieRatingReference {
	
	private String reviewTitle;
	
	private String review;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private LocalDateTime createdDate;
	
	public String getReviewTitle() {
		return reviewTitle;
	}

	public String getReview() {
		return review;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
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
