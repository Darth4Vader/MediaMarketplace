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

public class ProductDto {

	@NotBlank
	private Long productId;
	
	@NotBlank
    private Long movieId;
	
	private double buyPrice;
	
	private double rentPrice;
	
	private double buyDiscount;
	
	private double rentDiscount;
	
	public ProductDto() {
		//BigDecimal db = new BigDecimal(0)..setScale(12, BigDecimal.ROUND_HALF_UP);
	}

	public ProductDto(@NotBlank Long productId, @NotBlank Long movieId, double buyPrice, double rentPrice,
			double buyDiscount, double rentDiscount) {
		super();
		this.productId = productId;
		this.movieId = movieId;
		this.buyPrice = buyPrice;
		this.rentPrice = rentPrice;
		this.buyDiscount = buyDiscount;
		this.rentDiscount = rentDiscount;
	}

	public Long getProductId() {
		return productId;
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

	public double getBuyDiscount() {
		return buyDiscount;
	}

	public double getRentDiscount() {
		return rentDiscount;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
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

	public void setBuyDiscount(double buyDiscount) {
		this.buyDiscount = buyDiscount;
	}

	public void setRentDiscount(double rentDiscount) {
		this.rentDiscount = rentDiscount;
	}

}
