package backend.dto.mediaProduct;

import jakarta.validation.constraints.NotBlank;

public class ProductDto {
	
	private Long id;
	private MovieReference movie;
	private double finalBuyPrice;
	private double finalRentPrice;
	
	public MovieReference getMovie() {
		return movie;
	}
	
	public double getFinalBuyPrice() {
		return finalBuyPrice;
	}
	
	public double getFinalRentPrice() {
		return finalRentPrice;
	}
	
	public void setMovie(MovieReference movie) {
		this.movie = movie;
	}
	
	public void setFinalBuyPrice(double finalBuyPrice) {
		this.finalBuyPrice = finalBuyPrice;
	}
	
	public void setFinalRentPrice(double finalRentPrice) {
		this.finalRentPrice = finalRentPrice;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}