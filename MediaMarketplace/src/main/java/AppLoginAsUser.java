import org.springframework.context.ConfigurableApplicationContext;

import backend.controllers.CartController;
import backend.controllers.OrderController;
import backend.controllers.UserAuthenticateController;
import backend.dtos.references.CartProductReference;
import backend.dtos.users.LogInDto;
import backend.entities.User;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.PurchaseOrderException;
import backend.services.OrderService;
import frontend.App;
import frontend.AppImageUtils;
import frontend.homePage.HomePageController;
import frontend.utils.AppUtils;
import javafx.application.Platform;
import javafx.stage.Stage;

public class AppLoginAsUser extends App {
	
	public static void main(String[] args) throws Exception {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) {
		super.start(stage);
		ConfigurableApplicationContext context = getContext();
		if(context != null) {
			UserAuthenticateController userAuth = context.getBean(UserAuthenticateController.class);
			
			LogInDto dto = new LogInDto("frodo", "bag");
			//LogInDto dto = new LogInDto("bilbo", "bag2");
			try {
				userAuth.loginUser(dto);
				//userAuth.registerUser(new UserInformationDto("frodo", "", "bag", "bag"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			OrderController orderController = (OrderController) context.getBean(OrderController.class);
			CartController cartController = (CartController) context.getBean(CartController.class);
			CartProductReference cartProductReference = new CartProductReference();
			cartProductReference.setProductId(36l);
			for(int i = 0;i < 2; i++)
			new Thread(() -> {
				try {
					cartController.removeProductFromCart(cartProductReference);
				} catch (Exception e) {
					Thread.currentThread().getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
				}
				//System.out.println("\u001B[32m" + "Purchased successfuly" + "\u001B[0m");
				//cartService.addProductToCart(productReference, user);
			}).start();
		}
	}
}
