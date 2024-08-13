package backend.dto.mediaProduct;

import java.math.BigDecimal;

import backend.entities.Movie;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;

public class ProductReference {

	@NotBlank
	private Long id;
	
	@NotBlank
    private Long movieId;
	
	private double buyPrice;
	
	private double rentPrice;
	
	private BigDecimal buyDiscount;
	
	private BigDecimal rentDiscount;
	
	public Long getId() {
		return id;
	}

	public Long getMovieId() {
		return movieId;
	}

	public double getBuyPrice() {
		return buyPrice;
	}

	public double getRentPrice() {
		return rentPrice;
	}

	public BigDecimal getBuyDiscount() {
		return buyDiscount;
	}

	public BigDecimal getRentDiscount() {
		return rentDiscount;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setMovieId(Long movieId) {
		this.movieId = movieId;
	}

	public void setBuyPrice(double buyPrice) {
		this.buyPrice = buyPrice;
	}

	public void setRentPrice(double rentPrice) {
		this.rentPrice = rentPrice;
	}

	public void setBuyDiscount(BigDecimal buyDiscount) {
		this.buyDiscount = buyDiscount;
	}

	public void setRentDiscount(BigDecimal rentDiscount) {
		this.rentDiscount = rentDiscount;
	}

}
