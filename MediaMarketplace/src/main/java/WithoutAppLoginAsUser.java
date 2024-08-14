import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import backend.ActivateSpringApplication;
import backend.controllers.OrderController;
import backend.controllers.UserAuthenticateController;
import backend.dto.cart.CartProductReference;
import backend.dto.mediaProduct.ProductReference;
import backend.dto.users.LogInDto;
import backend.dto.users.LogInResponseDto;
import backend.entities.User;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.EntityRemovalException;
import backend.exceptions.PurchaseOrderException;
import backend.services.CartService;
import backend.services.OrderService;
import backend.services.ProductService;
import backend.services.TokenService;

public class WithoutAppLoginAsUser {

	private static ConfigurableApplicationContext appContext;
	
	public static void main(String[] args) {
		appContext = ActivateSpringApplication.create(args);
		UserAuthenticateController userAuth = appContext.getBean(UserAuthenticateController.class);
		
		LogInDto dto = new LogInDto("frodo", "bag");
		//LogInDto dto = new LogInDto("bilbo", "bag");
		try {
			LogInResponseDto d = userAuth.loginUser(dto);
			//userAuth.registerUser(new UserInformationDto("frodo", "", "bag", "bag"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		CartService cartService = appContext.getBean(CartService.class);
		CartProductReference productReference = new CartProductReference();
		productReference.setBuying(false);
		productReference.setProductId(32l);
		try {
			cartService.addProductToCart(productReference);
		} catch (Exception e3) {
			e3.printStackTrace();
			// TODO: handle exception
		}
		
		OrderService orderService = appContext.getBean(OrderService.class);
		TokenService tokenService = appContext.getBean(TokenService.class);
		
		OrderController orderController = appContext.getBean(OrderController.class);
		
		User user = tokenService.getCurretUser();
		for(int i = 0; i < 8; i++) {
			int j = i;
			new Thread(() -> {
				try {
					userAuth.loginUser(dto);
					//cartService.removeProductFromCart(productReference, user);
					orderController.placeOrder();
					System.out.println("\u001B[32m" + "Purchased successfuly" + "\u001B[0m");
					//cartService.addProductToCart(productReference, user);
				} catch (Exception e) {
					System.err.println(e.getMessage());
					//e.printStackTrace();
				}
			}).start();
		}
		
	}

}
