package backend.entities;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

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
	
	/*@Column(name = "expiration_date", nullable = false)
	private Date expirationDate;*/
	
    @ManyToOne
    @JoinColumn(name = "movie_id", referencedColumnName = "id", nullable = false)
    private Movie movie;
    
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    //@JsonIgnore
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

	public MoviePurchased() {
		Date date = null;
		//Calendar c = Calendar.Builder().
	}

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
		/*LocalDateTime date = this.order.getPurchasedDate();
		if(date != null)
			setPurchaseDate(date);*/
	}

	public LocalDateTime getPurchaseDate() {
		return purchaseDate;
	}

	public Date getExpirationDate() {
		return null;
		//return expirationDate;
	}

	public void setPurchaseDate(@Nonnull LocalDateTime purchaseDate) {
		this.purchaseDate = purchaseDate;
	}
	
	public boolean isUseable() {
		if(!isRented)
			return true;
		LocalDateTime timeSince = getCurrentRentTime();
		LocalDateTime now = LocalDateTime.now();
		return isRented == false || now.isBefore(timeSince);
		//return new Date().before(expirationDate);
	}
	
	public LocalDateTime getCurrentRentTime() {
		LocalDateTime timeSince = purchaseDate.plusSeconds(rentTime.getSeconds());
				/*.plusSeconds(rentTime.getSeconds())
				.plusMinutes(rentTime.day())
				.plusHours(rentTime.getHour())
				.plusDays(rentTime.getDayOfMonth())
				.plusMonths(rentTime.getMonthValue());*/
		return timeSince;
	}
	
	public Duration getRemainTime() {
		LocalDateTime timeSince = getCurrentRentTime();
		LocalDateTime now = LocalDateTime.now();
		try {
			return Duration.between(now, timeSince);
		}
		catch (Exception e) {
			return null;
		}
	}

	public boolean isRented() {
		return isRented;
	}

	public Duration getRentTime() {
		return rentTime;
	}
	
	/*public Duration getRentTimeRemaining() {
		LocalDateTime timeSince = getCurrentRentTime();
		LocalDateTime now = LocalDateTime.now();
		try {
			return rentTime.minus(Duration.between(timeSince, now));
		}
		catch (ArithmeticException e) {
			return null;
		}
	}*/

	public void setRented(boolean isRented) {
		this.isRented = isRented;
	}

	public void setRentTime(Duration rentTime) {
		this.rentTime = rentTime;
	}

}
