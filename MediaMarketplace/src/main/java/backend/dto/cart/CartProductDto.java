package backend.dto.cart;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CartProductDto {
	
	private @NotNull Long userId;
	
	private @NotNull Long productId;
	
	private boolean isBuying;

	public CartProductDto() {}
	
	public CartProductDto(Long userId, Long productId) {
		super();
		this.userId = userId;
		this.productId = productId;
	}

	public Long getUserId() {
		return userId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public boolean isBuying() {
		return isBuying;
	}

	public void setBuying(boolean isBuying) {
		this.isBuying = isBuying;
	}

}
