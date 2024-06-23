package backend.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "movie_reviews")
public class MovieReview {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(columnDefinition = "INT CHECK (rating <= 100 and rating >= 0)")
	private int rating;
	
	@Column(name = "review_title")
	private String reviewTitle;
	
	@Column(length = 1000)
	private String review;
	
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "movie_id", referencedColumnName = "id", nullable = false)
    private Movie movie;
    
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    //@JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

	public Long getId() {
		return id;
	}

	public int getRating() {
		return rating;
	}

	public String getReviewTitle() {
		return reviewTitle;
	}

	public String getReview() {
		return review;
	}

	public Movie getMovie() {
		return movie;
	}

	public User getUser() {
		return user;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public void setReviewTitle(String reviewTitle) {
		this.reviewTitle = reviewTitle;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public void setMovie(Movie movie) {
		this.movie = movie;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
