package backend.dto.mediaProduct;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import backend.entities.Movie;
import backend.entities.MoviePurchased;
import backend.entities.Order;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class MoviePurchasedDto {
	
	private Long id;
	private double purchasePrice;
	private LocalDateTime purchaseDate;
	private boolean isRented;
	private Duration rentTime;
	private LocalDateTime rentTimeSincePurchase;
    private MovieReference movie;
	
    public Long getId() {
		return id;
	}
    
	public double getPurchasePrice() {
		return purchasePrice;
	}
	
	public LocalDateTime getPurchaseDate() {
		return purchaseDate;
	}
	
	public boolean isRented() {
		return isRented;
	}
	
	public Duration getRentTime() {
		return rentTime;
	}
	
	public MovieReference getMovie() {
		return movie;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public void setPurchasePrice(double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}
	
	public void setPurchaseDate(LocalDateTime purchaseDate) {
		this.purchaseDate = purchaseDate;
	}
	
	public void setRented(boolean isRented) {
		this.isRented = isRented;
	}
	
	public void setRentTime(Duration rentTime) {
		this.rentTime = rentTime;
	}
	
	public void setMovie(MovieReference movie) {
		this.movie = movie;
	}

	public LocalDateTime getRentTimeSincePurchase() {
		return rentTimeSincePurchase;
	}

	public void setRentTimeSincePurchase(LocalDateTime rentTimeSincePurchase) {
		this.rentTimeSincePurchase = rentTimeSincePurchase;
	}
}
