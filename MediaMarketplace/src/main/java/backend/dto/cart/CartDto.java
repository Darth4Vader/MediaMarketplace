package backend.dto.cart;

import java.util.List;

import backend.entities.CartProduct;
import backend.entities.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

public class CartDto {
	
	private List<CartProductDto> cartProducts;
	private double totalPrice;
	
	public CartDto() {
		
	}
	
	public List<CartProductDto> getCartProducts() {
		return cartProducts;
	}
	
	public double getTotalPrice() {
		return totalPrice;
	}
	
	public void setCartProducts(List<CartProductDto> cartProducts) {
		this.cartProducts = cartProducts;
	}
	
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	
	
}