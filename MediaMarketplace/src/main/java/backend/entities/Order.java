package backend.entities;

import java.util.Date;
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
@Table(name = "orders")
public class Order {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "total_price")
	private double totalPrice;
	
	@Column(name = "purchased_price")
	private Date purchasedDate;
	
	@OneToMany(fetch = FetchType.LAZY, targetEntity = MediaPurchased.class)
	@JoinColumn(name = "genres", insertable = false, updatable = false)
	private List<MediaPurchased> purchasedItems;
	
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;

	public Order() {
	}

	public Long getId() {
		return id;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public Date getPurchasedDate() {
		return purchasedDate;
	}

	public List<MediaPurchased> getPurchasedItems() {
		return purchasedItems;
	}

	public User getUser() {
		return user;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public void setPurchasedDate(Date purchasedDate) {
		this.purchasedDate = purchasedDate;
	}

	public void setPurchasedItems(List<MediaPurchased> purchasedItems) {
		this.purchasedItems = purchasedItems;
	}

	public void setUser(User user) {
		this.user = user;
	}

}