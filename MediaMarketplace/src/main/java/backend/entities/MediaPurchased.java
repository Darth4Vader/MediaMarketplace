package backend.entities;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.annotation.Nonnull;
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
	
	@Column(name = "purchase_price", nullable = false)
	private double purchasePrice;
	
	@Column(name = "purchased_date", nullable = false)
	private Date purchaseDate;
	
	@Column(name = "expiration_date", nullable = false)
	private Date expirationDate;
	
    @ManyToOne
    @JoinColumn(name = "media_product_id", referencedColumnName = "id", nullable = false)
    private MediaProduct mediaProduct;
    
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    //@JsonIgnore
    @JoinColumn(name = "order_id")
    private Order order;

	public MediaPurchased() {
		Date date = null;
		//Calendar c = Calendar.Builder().
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
		Date date = this.order.getPurchasedDate();
		if(date != null)
			setPurchaseDate(date);
	}

	public Date getPurchaseDate() {
		return purchaseDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setPurchaseDate(@Nonnull Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(purchaseDate);
	    cal.add(Calendar.MINUTE, 1);
		this.expirationDate = cal.getTime();
	}
	
	public boolean isUseable() {
		return new Date().before(expirationDate);
	}

}
