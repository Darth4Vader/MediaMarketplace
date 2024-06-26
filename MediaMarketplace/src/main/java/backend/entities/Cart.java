package backend.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.Cascade;

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
@Table(name = "carts")
public class Cart {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CartProduct> cartProducts;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
	private User user;

	public Cart() {
		cartProducts = new ArrayList<>();
	}

	public Cart(User user) {
		this.cartProducts = new ArrayList<>();
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public List<CartProduct> getCartProducts() {
		return cartProducts;
	}

	public User getUser() {
		return user;
	}
	
	public void addToCartProducts(CartProduct cartProduct) {
		this.cartProducts.add(cartProduct);
		cartProduct.setCart(this);
	}

	public void setUser(User user) {
		this.user = user;
	}

}
