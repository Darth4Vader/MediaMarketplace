package backend.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "media_purchased")
public class MediaPurchased {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "purchase_price", nullable = false, unique = true)
	private double purchasePrice;
	
    @ManyToOne
    @JoinColumn(name = "Media_product_id", referencedColumnName = "id", nullable = false)
    private MediaProduct mediaProduct;
    
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "order_id", referencedColumnName = "id", nullable = false)
    private Order order;

	public MediaPurchased() {
	}

	public Long getId() {
		return id;
	}

	public double getPurchasePrice() {
		return purchasePrice;
	}

	public MediaProduct getMediaProduct() {
		return mediaProduct;
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

	public void setMediaProduct(MediaProduct mediaProduct) {
		this.mediaProduct = mediaProduct;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

}
