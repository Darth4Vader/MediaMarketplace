package backend.entities;

import java.util.List;

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
	
	@ManyToOne
	@JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
	private MediaProduct product;
	
	@ManyToOne
	@JoinColumn(name = "cart_id", referencedColumnName = "id")
	private Cart cart;

	public CartProduct() {
	}

	public CartProduct(MediaProduct product, Cart cart) {
		this.product = product;
		this.cart = cart;
	}

	public Long getId() {
		return id;
	}

	public MediaProduct getProduct() {
		return product;
	}

	public Cart getCart() {
		return cart;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setProduct(MediaProduct product) {
		this.product = product;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}

}
