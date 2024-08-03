package backend.entities;

import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
	
	public double getPrice() {
		return product.calculatePrice(isBuying());
	}

	@Override
	public String toString() {
		return "CartProduct [id=" + id + ", product=" + product + ", cart=" + cart + ", isBuying=" + isBuying + "]";
	}

}
