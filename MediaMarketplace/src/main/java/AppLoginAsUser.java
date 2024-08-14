import backend.controllers.UserAuthenticateController;
import backend.dto.users.LogInDto;
import backend.dto.users.LogInResponseDto;
import backend.entities.User;
import backend.exceptions.PurchaseOrderException;
import backend.services.OrderService;
import backend.services.TokenService;
import frontend.App;
import frontend.AppImageUtils;
import frontend.homePage.HomePageController;
import javafx.application.Platform;
import javafx.stage.Stage;

public class AppLoginAsUser extends App {
	
	public static void main(String[] args) throws Exception {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) {
		super.start(stage);
		UserAuthenticateController userAuth = App.getApplicationInstance().getContext().getBean(UserAuthenticateController.class);
		
		//LogInDto dto = new LogInDto("frodo", "bag");
		LogInDto dto = new LogInDto("bilbo", "bag");
		try {
			LogInResponseDto d = userAuth.loginUser(dto);
			//userAuth.registerUser(new UserInformationDto("frodo", "", "bag", "bag"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
