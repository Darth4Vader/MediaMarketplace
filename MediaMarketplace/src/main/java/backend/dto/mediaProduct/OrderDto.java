package backend.dto.mediaProduct;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;

import backend.entities.MoviePurchased;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

public class OrderDto {
	
	private Long id;
	private double totalPrice;
	private LocalDateTime purchasedDate;
	private List<MoviePurchasedDto> purchasedItems;
	
	public Long getId() {
		return id;
	}
	
	public double getTotalPrice() {
		return totalPrice;
	}
	
	public LocalDateTime getPurchasedDate() {
		return purchasedDate;
	}
	
	public List<MoviePurchasedDto> getPurchasedItems() {
		return purchasedItems;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	public void setPurchasedDate(LocalDateTime purchasedDate) {
		this.purchasedDate = purchasedDate;
	}
	
	public void setPurchasedItems(List<MoviePurchasedDto> purchasedItems) {
		this.purchasedItems = purchasedItems;
	}
}
