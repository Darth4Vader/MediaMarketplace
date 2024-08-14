package backend.entities;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.annotation.Nonnull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "movie_purchased")
@EntityListeners(AuditingEntityListener.class)
public class MoviePurchased {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "purchase_price", nullable = false)
	private double purchasePrice;
	
	@CreatedDate
	@Column(name = "purchased_date", nullable = false)
	private LocalDateTime purchaseDate;
	
	@Column(name = "is_rented")
	private boolean isRented;
	
	@Column(name = "rent_time")
	private Duration rentTime;
	
    @ManyToOne
    @JoinColumn(name = "movie_id", referencedColumnName = "id", nullable = false)
    private Movie movie;
    
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

	public Long getId() {
		return id;
	}

	public double getPurchasePrice() {
		return purchasePrice;
	}

	public Movie getMovie() {
		return movie;
	}

	public Order getOrder() {
		return order;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setPurchasePrice(double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public void setMovie(Movie movie) {
		this.movie = movie;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public LocalDateTime getPurchaseDate() {
		return purchaseDate;
	}
	
	public void setPurchaseDate(@Nonnull LocalDateTime purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public boolean isRented() {
		return isRented;
	}

	public Duration getRentTime() {
		return rentTime;
	}

	public void setRented(boolean isRented) {
		this.isRented = isRented;
	}

	public void setRentTime(Duration rentTime) {
		this.rentTime = rentTime;
	}

}
