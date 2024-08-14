package backend.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cart_products")
public class CartProduct {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//not working, not deleting, check google bookmark from jan 29 2024
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "product_id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Product product;
	
	@ManyToOne()
	@JoinColumn(name = "cart_id", referencedColumnName = "id", nullable = false)
	private Cart cart;
	
	@JoinColumn(name = "is_buying", nullable = false)
	private boolean isBuying;

	public CartProduct() {
	}

	public Long getId() {
		return id;
	}

	public Product getProduct() {
		return product;
	}

	public Cart getCart() {
		return cart;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}

	public boolean isBuying() {
		return isBuying;
	}

	public void setBuying(boolean isBuying) {
		this.isBuying = isBuying;
	}
}
