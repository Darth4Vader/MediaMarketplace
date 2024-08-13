package backend.dto.cart;

import backend.dto.mediaProduct.ProductDto;
import backend.dto.mediaProduct.ProductReference;

public class CartProductDto {
	
	private ProductDto product;
	private boolean isBuying;
	private double price;

	public CartProductDto() {
		// TODO Auto-generated constructor stub
	}

	public ProductDto getProduct() {
		return product;
	}

	public boolean isBuying() {
		return isBuying;
	}

	public void setProduct(ProductDto product) {
		this.product = product;
	}

	public void setBuying(boolean isBuying) {
		this.isBuying = isBuying;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

}
