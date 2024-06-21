package backend.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "products")
public class Product implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private double buyPrice;
	
	@Column(nullable = false)
	private double rentPrice;
	
	@Column(precision=5, scale=2)
	private BigDecimal buyDiscount;
	
	@Column(precision=5, scale=2)
	private BigDecimal rentDiscount;
	
	/*
	 *     @CreationTimestamp
    private LocalDateTime createdAt;s
	 */
	
	
	//@OneToMany(fetch = FetchType.LAZY, mappedBy = "movie", cascade = CascadeType.ALL)
    //@JoinColumn(name = "movie_id", nullable = false)
	//@ManyToOne(optional = false)
	@OneToOne(optional = false)
	@JoinColumn(name = "movie_id")
    private Movie movie;

	public Product() {
	}

	public Long getId() {
		return id;
	}

	public double getBuyPrice() {
		return buyPrice;
	}

	public double getRentPrice() {
		return rentPrice;
	}

	public double getBuyDiscount() {
		return buyDiscount.doubleValue();
	}

	public double getRentDiscount() {
		return rentDiscount.doubleValue();
	}

	public Movie getMovie() {
		return movie;
	}

	public void setBuyPrice(double buyPrice) {
		this.buyPrice = buyPrice;
	}

	public void setRentPrice(double rentPrice) {
		this.rentPrice = rentPrice;
	}
	
	public void setBuyDiscount(double buyDiscount) {
		setBuyDiscount(new BigDecimal(buyDiscount));
	}

	public void setBuyDiscount(BigDecimal buyDiscount) {
		this.buyDiscount = buyDiscount;
	}
	
	public void setRentDiscount(double rentDiscount) {
		setRentDiscount(new BigDecimal(rentDiscount));
	}

	public void setRentDiscount(BigDecimal rentDiscount) {
		this.rentDiscount = rentDiscount;
	}

	public void setMovie(Movie movie) {
		this.movie = movie;
	}
	
	private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
	
	public double calculatePrice(boolean isBuy) {
		double price;
		BigDecimal discount;
		if(isBuy) {
			price = getBuyPrice();
			discount = buyDiscount;
		}
		else {
			price = getRentPrice();
			discount = rentDiscount;
		}
		if(discount == null || discount.compareTo(BigDecimal.ZERO) == 0)
			return price;
		return ONE_HUNDRED.subtract(discount)
				.divide(ONE_HUNDRED)
				.multiply(new BigDecimal(price, new MathContext(2, RoundingMode.HALF_EVEN)))
				.doubleValue();
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", buyPrice=" + buyPrice + ", rentPrice=" + rentPrice + ", buyDiscount="
				+ buyDiscount + ", rentDiscount=" + rentDiscount + ", movie=" + movie + "]";
	}
	
}
