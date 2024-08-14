package backend.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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

@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
public class Order {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "total_price")
	private double totalPrice;
	
	@CreatedDate
	@Column(name = "purchased_date", nullable = false)
	private LocalDateTime purchasedDate;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "order", cascade = CascadeType.ALL)
	private List<MoviePurchased> purchasedItems;
	
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;

	public Order() {
		this.purchasedItems = new ArrayList<>();
	}

	public Long getId() {
		return id;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public LocalDateTime getPurchasedDate() {
		return purchasedDate;
	}

	public List<MoviePurchased> getPurchasedItems() {
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

	public void setPurchasedDate(LocalDateTime purchasedDate) {
		this.purchasedDate = purchasedDate;
		for(MoviePurchased purchased : this.purchasedItems)
			purchased.setPurchaseDate(purchasedDate);
	}

	public void addToPurchasedItems(MoviePurchased purchasedItems) {
		this.purchasedItems.add(purchasedItems);
		purchasedItems.setOrder(this);
	}

	public void setUser(User user) {
		this.user = user;
	}
}
