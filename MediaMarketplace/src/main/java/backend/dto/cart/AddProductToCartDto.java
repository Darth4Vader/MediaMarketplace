package backend.dto.cart;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AddProductToCartDto {
	
	private @NotNull Long userId;
	
	private @NotNull Long productId;

	public AddProductToCartDto() {}
	
	public AddProductToCartDto(Long userId, Long productId) {
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

}
